package net.rankedproject.privateapi.repository;

import net.rankedproject.privateapi.controller.RankedPlayer;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface RankedPlayerRepository extends MongoRepository<RankedPlayer, UUID> {

}