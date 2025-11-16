package net.rankedproject.spigot.spawn;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.common.util.EnvironmentUtil;
import net.rankedproject.spigot.CommonPlugin;
import net.rankedproject.spigot.world.SpawnFlag;
import org.bukkit.Material;
import org.bukkit.Particle;
import org.bukkit.World;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Set;

@Singleton
@RequiredArgsConstructor(onConstructor_={@Inject})
public class SpawnListener implements Listener {

    private static final Set<Material> BLOCKED_FOOD_MATERIAL = Set.of(
            Material.CHORUS_FRUIT
    );

    private static final Set<Material> BLOCKED_MATERIAL = Set.of(
            Material.ENDER_CHEST,
            Material.CHEST,
            Material.TRAPPED_CHEST,
            Material.ANVIL,
            Material.ENCHANTING_TABLE
    );

    private static final Set<Material> USABLE_MATERIAL = Set.of(
            Material.EXPERIENCE_BOTTLE
    );

    private static final Set<EntityType> USABLE_PROJECTILE = Set.of(
            EntityType.EXPERIENCE_BOTTLE
    );

    private final CommonPlugin plugin;

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        var player = event.getPlayer();
        if (EnvironmentUtil.isTesting()) {
            player.setOp(true);
        }

        var rankedServer = plugin.getRankedServer();
        var spawn = rankedServer.spawn();
        if (spawn == null) {
            return;
        }

        boolean isFlagEnabled = spawn.getFlags().contains(SpawnFlag.AUTO_TELEPORT);
        if (!isFlagEnabled) {
            return;
        }

        var location = spawn.getLocationFinder().apply(player);
        player.teleportAsync(location);
    }

    @EventHandler
    public void onProjectile(ProjectileLaunchEvent event) {
        if (isFlagNotEnabled(event.getEntity().getWorld(), SpawnFlag.NO_PROJECTILE)) {
            return;
        }

        if (!USABLE_PROJECTILE.contains(event.getEntity().getType())) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntitySpawn(CreatureSpawnEvent event) {
        if (event.getSpawnReason() == CreatureSpawnEvent.SpawnReason.CUSTOM) {
            return;
        }

        if (isFlagNotEnabled(event.getEntity().getWorld(), SpawnFlag.NO_CREATURE_SPAWN)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onEntityDamage(EntityDamageEvent event) {
        if (isFlagNotEnabled(event.getEntity().getWorld(), SpawnFlag.NO_ENTITY_DAMAGE)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onEntityDamageByEntity(EntityDamageByEntityEvent event) {
        if (isFlagNotEnabled(event.getEntity().getWorld(), SpawnFlag.NO_ENTITY_DAMAGE)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockBreak(BlockBreakEvent event) {
        if (isFlagNotEnabled(event.getBlock().getWorld(), SpawnFlag.NO_BLOCK_BREAK)) {
            return;
        }

        Player player = event.getPlayer();
        player.spawnParticle(
                Particle.SMOKE,
                event.getBlock().getLocation().add(0.5, 1, 0.5),
                8,
                0, 0, 0,
                0.05
        );

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlaceBlock(BlockPlaceEvent event) {
        if (isFlagNotEnabled(event.getBlock().getWorld(), SpawnFlag.NO_BLOCK_PLACE)) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onFoodChange(FoodLevelChangeEvent event) {
        if (isFlagNotEnabled(event.getEntity().getWorld(), SpawnFlag.NO_FOOD_CONSUME)) {
            return;
        }

        event.setFoodLevel(20);
        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerConsumeEvent(PlayerItemConsumeEvent event) {
        Player player = event.getPlayer();
        if (isFlagNotEnabled(player.getWorld(), SpawnFlag.NO_FOOD_CONSUME)) {
            return;
        }

        ItemStack itemStack = event.getItem();
        Material itemType = itemStack.getType();

        if (BLOCKED_FOOD_MATERIAL.contains(itemType)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onPlayerAction(PlayerInteractEvent event) {
        if (event.getAction() != Action.RIGHT_CLICK_BLOCK) {
            return;
        }

        if (isFlagNotEnabled(event.getPlayer().getWorld(), SpawnFlag.NO_RIGHT_CLICK)) {
            return;
        }

        ItemStack itemInHand = event.getItem();
        Material clickedBlockMaterial = Objects.requireNonNull(event.getClickedBlock()).getType();

        if (itemInHand != null && itemInHand.getType().isEdible() && !BLOCKED_MATERIAL.contains(clickedBlockMaterial)) return;
        if (itemInHand != null && USABLE_MATERIAL.contains(itemInHand.getType()) && !BLOCKED_MATERIAL.contains(clickedBlockMaterial)) return;

        event.setCancelled(true);
    }

    @EventHandler
    public void onBlockChange(EntityChangeBlockEvent event) {
        if (isFlagNotEnabled(event.getBlock().getWorld(), SpawnFlag.NO_DESTROY_CROP)) {
            return;
        }

        if (event.getBlock().getType() != Material.FARMLAND) {
            return;
        }

        event.setCancelled(true);
    }

    @EventHandler
    public void onPlayerTeleport(PlayerTeleportEvent event) {
        Player player = event.getPlayer();
        if (isFlagNotEnabled(player.getWorld(), SpawnFlag.NO_TELEPORT)) {
            return;
        }

        player.setHealth(20);
        player.setSaturation(20);
        player.setFoodLevel(20);
    }

    private boolean isFlagNotEnabled(@NotNull World world, @NotNull SpawnFlag spawnFlag) {
        var rankedServer = plugin.getRankedServer();
        var spawn = rankedServer.spawn();
        if (spawn == null) {
            return true;
        }

        var expectedWorldName = spawn.getWorldName();
        if (!expectedWorldName.equals(world.getName())) {
            return true;
        }

        return !spawn.getFlags().contains(spawnFlag);
    }
}
