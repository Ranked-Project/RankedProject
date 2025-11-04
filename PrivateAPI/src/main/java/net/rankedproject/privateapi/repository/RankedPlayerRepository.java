package net.rankedproject.privateapi.repository;

import net.rankedproject.privateapi.controller.RankedPlayer;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface RankedPlayerRepository extends MongoRepository<RankedPlayer, UUID> {

}