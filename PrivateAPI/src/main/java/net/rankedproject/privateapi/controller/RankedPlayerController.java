package net.rankedproject.privateapi.controller;

import lombok.RequiredArgsConstructor;
import net.rankedproject.privateapi.service.RankedPlayerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping(path = "/api/v1/ranked/players")
@RequiredArgsConstructor
public class RankedPlayerController {

    private final RankedPlayerService service;

    @GetMapping
    public List<ResponseEntity<RankedPlayer>> getAllPlayers() {
        return service.getAllPlayers()
                .stream()
                .map(players -> new ResponseEntity<>(players, HttpStatus.OK))
                .toList();
    }

    @GetMapping(path = "/{id}")
    public ResponseEntity<RankedPlayer> getPlayerById(@PathVariable UUID id) {
        return service.getPlayerByIdOrCreate(id)
                .stream()
                .map(player -> new ResponseEntity<>(player, HttpStatus.OK))
                .findFirst()
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<RankedPlayer> savePlayer(@RequestBody RankedPlayer player) {
        return service.savePlayer(player)
                .stream()
                .map(savedPlayer -> new ResponseEntity<>(savedPlayer, HttpStatus.CREATED))
                .findFirst()
                .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @PutMapping(path = "/{id}")
    public ResponseEntity<RankedPlayer> updatePlayer(@RequestBody RankedPlayer player) {
        return service.updatePlayer(player)
                .stream()
                .map(updatedPlayer -> new ResponseEntity<>(updatedPlayer, HttpStatus.CREATED))
                .findFirst()
                .orElse(new ResponseEntity<>(HttpStatus.BAD_REQUEST));
    }

    @DeleteMapping(path = "/{id}")
    public ResponseEntity<Void> deletePlayer(@PathVariable UUID id) {
        service.deleteItem(id);
        return ResponseEntity.noContent().build();
    }
}