package com.ming.game.gamedata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/gameData")
public class GameDataController {
    @Autowired
    private GameDataService service;

    @GetMapping
    public GameData getGameData() {
        return getGameDataByLevel(1);
    }

    @GetMapping("/{level}")
    public GameData getGameDataByLevel(@PathVariable int level) {
        return service.getDataByLevel(level);
    }

    @GetMapping("/levelsAmount")
    public int getLevelsAmount() {
        return service.getLevelsAmount();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    GameData addGameData(@RequestBody GameData gameData) {
        return service.addGameData(gameData);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{level}")
    GameData updateGameData(@PathVariable int level, @RequestBody GameData gameData) {
        String id = service.getDataByLevel(level).getId();
        gameData.setId(id);
        return service.updateGameData(gameData);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{level}")
    GameData removeGameData(@PathVariable int level) {
        return service.removeGameData(level);
    }
}
