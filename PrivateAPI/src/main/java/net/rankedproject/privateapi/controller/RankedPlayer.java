package net.rankedproject.privateapi.controller;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.UUID;

@Data
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