package com.ming.game.gamedata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class MongoGameDataServiceImpl implements GameDataService {
    @Autowired
    private GameDataRepository repository;

    @Override
    public GameData getDataByLevel(int level) {
        List<GameData> data = repository.findByLevel(level);
        if (data.size() == 0) {
            throw new IllegalArgumentException("No level " + level + " data exist.");
        }
        return data.get(0);
    }

    @Override
    public GameData addGameData(GameData gameData) {
        return repository.insert(gameData);
    }

    @Override
    public GameData updateGameData(GameData gameData) {
        repository.save(gameData);
        return gameData;
    }

    @Override
    public GameData removeGameData(int level) {
        return repository.deleteByLevel(level).get(0);
    }

    @Override
    public int getLevelsAmount() {
        return (int) repository.count();
    }
}
