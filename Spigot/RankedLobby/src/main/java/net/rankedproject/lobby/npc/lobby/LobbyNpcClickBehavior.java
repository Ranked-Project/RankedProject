package net.rankedproject.lobby.npc.lobby;

import com.google.inject.Inject;
import com.google.inject.Singleton;
import lombok.RequiredArgsConstructor;
import net.rankedproject.spigot.npc.click.NpcClickBehavior;
import net.rankedproject.spigot.npc.click.NpcClickBehaviorMetadata;
import org.jetbrains.annotations.NotNull;

@Singleton
@RequiredArgsConstructor(onConstructor_ = {@Inject})
public class LobbyNpcClickBehavior extends NpcClickBehavior {

    @Override
    public @NotNull NpcClickBehaviorMetadata behavior() {
        return NpcClickBehaviorMetadata.builder()
                .onClick(player -> player.sendMessage("Lobby clicked"))
                .build();
    }
}
