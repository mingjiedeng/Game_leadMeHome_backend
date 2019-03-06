package com.ming.game.gamedata;

public interface GameDataService {
    GameData getDataByLevel(int level);
    GameData addGameData(GameData gameData);
    GameData updateGameData(GameData gameData);
    GameData removeGameData(int level);
    int getLevelsAmount();
}
