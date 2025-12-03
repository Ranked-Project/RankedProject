package net.rankedproject.common.data.domain;

import java.util.UUID;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@RequiredArgsConstructor
public abstract class BasePlayer {
    private UUID id;
}
