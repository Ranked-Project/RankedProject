package net.rankedproject.common.util;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum ServerType {

    RANKED_LOBBY("ranked_lobby"),
    RANKED_SKYWARS_GAME("ranked_skywars_game");

    private final String identifier;
}
