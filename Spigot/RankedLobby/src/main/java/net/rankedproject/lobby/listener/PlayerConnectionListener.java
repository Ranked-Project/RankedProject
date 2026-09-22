package net.rankedproject.lobby.listener;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import kr.toxicity.model.api.BetterModel;
import com.sun.net.httpserver.HttpServer;
import io.papermc.paper.event.player.PlayerClientLoadedWorldEvent;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.rankedproject.spigot.CommonPlugin;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerResourcePackStatusEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.scheduler.BukkitTask;

import java.net.InetSocketAddress;
import java.net.URI;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.HashSet;
import java.util.HexFormat;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PlayerConnectionListener implements Listener {

    private final CommonPlugin plugin;
    private final Map<UUID, BukkitTask> pending = new HashMap<>();
    private final Map<UUID, BukkitTask> scheduledRequests = new HashMap<>();
    private final Set<UUID> requested = new HashSet<>();
    private HttpServer downloadServer;
    private ExecutorService downloads;
    private byte[] hash;
    private UUID packId;
    private String downloadPath;

    @EventHandler
    public void onServerLoad(ServerLoadEvent event) {
        if (downloadServer != null) {
            return;
        }
        try {
            if (BetterModel.model("ranked_bust").isEmpty()) {
                throw new IllegalStateException("BetterModel did not load ranked_bust.bbmodel");
            }
            if (BetterModel.config().packType() != kr.toxicity.model.api.BetterModelConfig.PackType.ZIP) {
                throw new IllegalStateException("Set BetterModel pack-type to zip and restart the server");
            }
            var packFile = BetterModel.plugin().getDataFolder().toPath().getParent()
                    .resolve(BetterModel.config().buildFolderLocation() + ".zip");
            // Serve an immutable snapshot so the downloaded bytes always match the advertised SHA-1.
            var pack = Files.readAllBytes(packFile);
            hash = MessageDigest.getInstance("SHA-1").digest(pack);
            packId = UUID.nameUUIDFromBytes(hash);
            downloadPath = "/ranked-" + HexFormat.of().formatHex(hash) + ".zip";
            var config = plugin.getConfig();
            downloadServer = HttpServer.create(new InetSocketAddress(
                    config.getString("resource-pack.bind-address", "0.0.0.0"),
                    config.getInt("resource-pack.port", 8081)), 0);
            downloads = Executors.newFixedThreadPool(2, runnable -> {
                var thread = new Thread(runnable, "ranked-resource-pack");
                thread.setDaemon(true);
                return thread;
            });
            downloadServer.setExecutor(downloads);
            downloadServer.createContext("/", exchange -> {
                try (exchange) {
                    var path = exchange.getRequestURI().getPath();
                    if (!path.equals(downloadPath) && !path.equals("/ranked.zip")) {
                        exchange.sendResponseHeaders(404, -1);
                        return;
                    }
                    var method = exchange.getRequestMethod();
                    if (!method.equals("GET") && !method.equals("HEAD")) {
                        exchange.getResponseHeaders().set("Allow", "GET, HEAD");
                        exchange.sendResponseHeaders(405, -1);
                        return;
                    }
                    exchange.getResponseHeaders().set("Content-Type", "application/zip");
                    exchange.getResponseHeaders().set("Content-Length", Integer.toString(pack.length));
                    exchange.getResponseHeaders().set("Cache-Control", path.equals("/ranked.zip")
                            ? "no-cache" : "public, max-age=31536000, immutable");
                    exchange.sendResponseHeaders(200, method.equals("HEAD") ? -1 : pack.length);
                    if (method.equals("GET")) {
                        exchange.getResponseBody().write(pack);
                    }
                }
            });
            downloadServer.start();
            plugin.getLogger().info("Required NPC resource pack ready on port "
                    + downloadServer.getAddress().getPort() + downloadPath);
        } catch (Exception exception) {
            stop();
            plugin.getLogger().log(Level.SEVERE, "Cannot prepare the required NPC resource pack; joins will be refused", exception);
        }
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // SpawnListener teleports asynchronously on join. Sending the prompt on the next
        // tick can race the client's loading/respawn screen and leave it unanswered.
        scheduleRequest(event.getPlayer(), 40L);
    }

    @EventHandler
    public void onClientLoadedWorld(PlayerClientLoadedWorldEvent event) {
        scheduleRequest(event.getPlayer(), 20L);
    }

    @EventHandler
    public void onPlayerChangedWorld(PlayerChangedWorldEvent event) {
        scheduleRequest(event.getPlayer(), 40L);
    }

    private void scheduleRequest(Player player, long delay) {
        var playerId = player.getUniqueId();
        if (requested.contains(playerId)) {
            return;
        }
        var previous = scheduledRequests.remove(playerId);
        if (previous != null) {
            previous.cancel();
        }
        scheduledRequests.put(playerId, Bukkit.getScheduler().runTaskLater(plugin, () -> {
            scheduledRequests.remove(playerId);
            requirePack(player);
        }, delay));
    }

    private void requirePack(Player player) {
        if (!player.isOnline()) {
            return;
        }
        if (downloadServer == null) {
            player.kick(Component.text("The required server resource pack is unavailable. Please try again later."));
            return;
        }
        try {
            var url = plugin.getConfig().getString("resource-pack.public-url", "").trim();
            if (url.isEmpty()) {
                var virtualHost = player.getVirtualHost();
                if (virtualHost == null) {
                    throw new IllegalStateException("Set resource-pack.public-url for this server");
                }
                url = new URI("http", null, virtualHost.getHostString(),
                        downloadServer.getAddress().getPort(), downloadPath, null, null).toASCIIString();
            }
            var uri = URI.create(url);
            if (!("http".equalsIgnoreCase(uri.getScheme()) || "https".equalsIgnoreCase(uri.getScheme()))
                    || uri.getHost() == null) {
                throw new IllegalArgumentException("resource-pack.public-url must be an HTTP(S) download URL");
            }
            cancelTimeout(player.getUniqueId());
            pending.put(player.getUniqueId(), Bukkit.getScheduler().runTaskLater(plugin, () -> {
                pending.remove(player.getUniqueId());
                player.kick(Component.text("You must load the server resource pack to play."));
            }, Math.max(1, plugin.getConfig().getInt("resource-pack.timeout-seconds", 120)) * 20L));
            // Add this pack without issuing setResourcePack's global pack-pop packet.
            player.addResourcePack(packId, url, hash,
                    "This server requires its resource pack to display custom NPCs.", true);
            requested.add(player.getUniqueId());
            plugin.getLogger().info("Sent required NPC resource pack to " + player.getName()
                    + ": " + url + " (id=" + packId + ")");
        } catch (RuntimeException | java.net.URISyntaxException exception) {
            cancelTimeout(player.getUniqueId());
            plugin.getLogger().log(Level.SEVERE, "Cannot send the required resource pack", exception);
            player.kick(Component.text("The required server resource pack is unavailable. Please contact an administrator."));
        }
    }

    @EventHandler
    public void onResourcePackStatus(PlayerResourcePackStatusEvent event) {
        if (packId == null || !packId.equals(event.getID())) {
            return;
        }
        plugin.getLogger().info("NPC resource pack for " + event.getPlayer().getName()
                + ": " + event.getStatus());
        switch (event.getStatus()) {
            case SUCCESSFULLY_LOADED -> cancelTimeout(event.getPlayer().getUniqueId());
            case ACCEPTED, DOWNLOADED -> { /* Wait for the pack to finish applying. */ }
            default -> {
                cancelTimeout(event.getPlayer().getUniqueId());
                event.getPlayer().kick(Component.text("You must accept and successfully load the server resource pack to play."));
            }
        }
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        var playerId = event.getPlayer().getUniqueId();
        cancelTimeout(playerId);
        requested.remove(playerId);
        var scheduled = scheduledRequests.remove(playerId);
        if (scheduled != null) {
            scheduled.cancel();
        }
    }

    @EventHandler
    public void onPluginDisable(PluginDisableEvent event) {
        if (event.getPlugin() == plugin) {
            stop();
        }
    }

    private void cancelTimeout(UUID playerId) {
        var timeout = pending.remove(playerId);
        if (timeout != null) {
            timeout.cancel();
        }
    }

    private void stop() {
        scheduledRequests.values().forEach(BukkitTask::cancel);
        scheduledRequests.clear();
        requested.clear();
        pending.values().forEach(BukkitTask::cancel);
        pending.clear();
        if (downloadServer != null) {
            downloadServer.stop(0);
            downloadServer = null;
        }
        if (downloads != null) {
            downloads.shutdownNow();
            downloads = null;
        }
    }
}
