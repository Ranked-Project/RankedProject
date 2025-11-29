package net.rankedproject.privateapi.controller;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Getter
@Setter
@RequiredArgsConstructor
@AllArgsConstructor
@Document(collection = "ranked_player")
public class RankedPlayer {

    @Id
    private UUID id;
    private String lastSeenName, ipAddress;

    private int kills, deaths, wins, losses;

    public RankedPlayer(UUID id) {
        this.id = id;
    }
}