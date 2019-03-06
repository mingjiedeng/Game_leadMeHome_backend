package com.ming.game.gamedata;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
@RepositoryRestResource(collectionResourceRel = "gameData", path = "gameData")
public interface GameDataRepository extends MongoRepository<GameData, String> {
    List<GameData> findByLevel(int level);
    List<GameData> deleteByLevel(int level);
}
