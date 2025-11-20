package net.rankedproject.spigot.npc.executor.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.npc.NPC;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rankedproject.spigot.npc.executor.LoadedNpc;
import net.rankedproject.spigot.npc.executor.NpcSpawnExecutor;
import net.rankedproject.spigot.npc.model.impl.PlayerNpcModel;
import net.rankedproject.spigot.npc.type.PlayerNpc;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class PlayerNpcSpawnExecutor implements NpcSpawnExecutor {

    private static final WrapperPlayServerTeams.ScoreBoardTeamInfo NAME_TAG_REMOVAL_TEAM = new WrapperPlayServerTeams.ScoreBoardTeamInfo(
            Component.text("NPC"),
            null,
            null,
            WrapperPlayServerTeams.NameTagVisibility.NEVER,
            WrapperPlayServerTeams.CollisionRule.NEVER,
            NamedTextColor.WHITE,
            WrapperPlayServerTeams.OptionData.NONE
    );

    @Override
    public void spawnEntity(LoadedNpc loadedNpc, UUID playerUUID) {
        var npc = loadedNpc.npc();
        var playerNpc = (PlayerNpc) npc;
        var npcModel = (PlayerNpcModel) playerNpc.getBehavior().model();

        var npcUserProfile = new UserProfile(UUID.randomUUID(), "NPC");
        if (npcModel != null) {
            npcUserProfile.setTextureProperties(List.of(
                    new TextureProperty("textures", npcModel.getTexture(), npcModel.getSignature())
            ));
        }

        showNpcEntity(playerUUID, loadedNpc, npcUserProfile);
    }

    private void showNpcEntity(
            UUID playerUUID,
            LoadedNpc loadedNpc,
            UserProfile profile
    ) {
        var npc = loadedNpc.npc();
        var playerNpc = (PlayerNpc) npc;

        var packetNpc = new NPC(profile, loadedNpc.entityId(), null);
        var location = playerNpc.getBehavior().location();

        var player = Objects.requireNonNull(Bukkit.getPlayer(playerUUID));
        var packetPlayerManager = PacketEvents.getAPI().getPlayerManager();

        packetNpc.setLocation(SpigotConversionUtil.fromBukkitLocation(location));
        packetNpc.spawn(packetPlayerManager.getChannel(player));

        hideNpcEntityName(player);
    }

    private void hideNpcEntityName(Player player) {
        var packetPlayerManager = PacketEvents.getAPI().getPlayerManager();
        packetPlayerManager.sendPacket(player, new WrapperPlayServerTeams(
                "NPC",
                WrapperPlayServerTeams.TeamMode.REMOVE,
                (WrapperPlayServerTeams.ScoreBoardTeamInfo) null
        ));

        packetPlayerManager.sendPacket(player, new WrapperPlayServerTeams(
                "NPC",
                WrapperPlayServerTeams.TeamMode.CREATE,
                NAME_TAG_REMOVAL_TEAM
        ));

        packetPlayerManager.sendPacket(player, new WrapperPlayServerTeams(
                "NPC",
                WrapperPlayServerTeams.TeamMode.ADD_ENTITIES,
                (WrapperPlayServerTeams.ScoreBoardTeamInfo) null,
                "NPC"
        ));
    }
}