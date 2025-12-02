package net.rankedproject.spigot.npc.executor.impl;

import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.attribute.Attributes;
import com.github.retrooper.packetevents.protocol.npc.NPC;
import com.github.retrooper.packetevents.protocol.player.TextureProperty;
import com.github.retrooper.packetevents.protocol.player.UserProfile;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerUpdateAttributes;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import lombok.RequiredArgsConstructor;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import net.rankedproject.spigot.npc.executor.LoadedNpc;
import net.rankedproject.spigot.npc.executor.NpcSpawnExecutor;
import net.rankedproject.spigot.npc.model.impl.PlayerNpcModel;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

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

    private static final WrapperPlayServerTeams REMOVE_TEAM_TAG_PACKET = new WrapperPlayServerTeams(
            "NPC",
            WrapperPlayServerTeams.TeamMode.CREATE,
            NAME_TAG_REMOVAL_TEAM
    );

    private static final WrapperPlayServerTeams REMOVE_TEAM_PACKET = new WrapperPlayServerTeams(
            "NPC",
            WrapperPlayServerTeams.TeamMode.REMOVE,
            (WrapperPlayServerTeams.ScoreBoardTeamInfo) null
    );

    private static final WrapperPlayServerTeams ADD_ENTITIES_PACKET = new WrapperPlayServerTeams(
            "NPC",
            WrapperPlayServerTeams.TeamMode.ADD_ENTITIES,
            (WrapperPlayServerTeams.ScoreBoardTeamInfo) null,
            "NPC"
    );

    @Override
    public void spawnEntity(@NotNull LoadedNpc loadedNpc, @NotNull UUID playerUUID) {
        var npc = loadedNpc.npc();
        var npcModel = (PlayerNpcModel) npc.getBehavior().model();

        var npcUserProfile = new UserProfile(UUID.randomUUID(), "NPC");
        if (npcModel != null) {
            npcUserProfile.setTextureProperties(List.of(
                    new TextureProperty("textures", npcModel.getTexture(), npcModel.getSignature())
            ));
        }

        showNpcEntity(playerUUID, loadedNpc, npcUserProfile);
    }

    private void showNpcEntity(
            @NotNull UUID playerUUID,
            @NotNull LoadedNpc loadedNpc,
            @NotNull UserProfile profile
    ) {
        var npc = loadedNpc.npc();

        var packetNpc = new NPC(profile, loadedNpc.entityId(), Component.empty());
        var location = npc.getBehavior().location();

        var player = Objects.requireNonNull(Bukkit.getPlayer(playerUUID));
        var packetPlayerManager = PacketEvents.getAPI().getPlayerManager();

        packetNpc.setLocation(SpigotConversionUtil.fromBukkitLocation(location));
        packetNpc.spawn(packetPlayerManager.getChannel(player));
        hideNpcEntityName(player);

        int entitySize = loadedNpc.npc().getBehavior().entitySize();
        var entitySizeProperty = new WrapperPlayServerUpdateAttributes.Property(Attributes.SCALE, entitySize, List.of());

        var updateAttributesPacket = new WrapperPlayServerUpdateAttributes(loadedNpc.entityId(), List.of(entitySizeProperty));
        packetPlayerManager.sendPacket(player, updateAttributesPacket);
    }

    private void hideNpcEntityName(@NotNull Player player) {
        var packetPlayerManager = PacketEvents.getAPI().getPlayerManager();

        packetPlayerManager.sendPacket(player, REMOVE_TEAM_PACKET);
        packetPlayerManager.sendPacket(player, REMOVE_TEAM_TAG_PACKET);
        packetPlayerManager.sendPacket(player, ADD_ENTITIES_PACKET);
    }
}
