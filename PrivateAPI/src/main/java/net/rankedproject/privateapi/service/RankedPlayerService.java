package net.rankedproject.privateapi.service;

import lombok.RequiredArgsConstructor;
import net.rankedproject.privateapi.controller.RankedPlayer;
import net.rankedproject.privateapi.repository.RankedPlayerRepository;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RankedPlayerService {

    private final RankedPlayerRepository repository;

    @Cacheable(value = "rankedPlayers")
    public List<RankedPlayer> getAllPlayers() {
        return repository.findAll();
    }

    @Cacheable(value = "rankedPlayers", key = "#id")
    public Optional<RankedPlayer> getPlayerById(UUID id) {
        return repository.findById(id);
    }

    @Cacheable(value = "rankedPlayers")
    public Optional<RankedPlayer> getPlayerByIdOrCreate(UUID id) {
        return Optional.of(repository.findById(id)
                .stream()
                .findFirst()
                .orElseGet(() -> {
                    RankedPlayer rankedPlayer = new RankedPlayer();
                    rankedPlayer.setId(id);
                    return repository.save(rankedPlayer);
                }));
    }

    @CachePut(value = "rankedPlayers", key = "#player.id")
    public Optional<RankedPlayer> savePlayer(RankedPlayer player) {
        return Optional.of(repository.save(player));
    }

    @CachePut(value = "rankedPlayers", key = "#player.id")
    public Optional<RankedPlayer> updatePlayer(RankedPlayer player) {
        return Optional.of(repository.save(player));
    }

    @CacheEvict(value = "rankedPlayers", key = "#id")
    public void deleteItem(UUID id) {
        repository.deleteById(id);
    }
}