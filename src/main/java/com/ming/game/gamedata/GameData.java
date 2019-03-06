package com.ming.game.gamedata;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.Data;
import org.springframework.data.annotation.Id;

@Data
public class GameData {
    @JsonIgnore
    @Id
    private String id;
    private int level;
    private Home home;
    private Hole[] holes;
    private Barrier[] barriers;
    private Ball[] balls;

    public GameData(int level) {
        this.level = level;
    }

    public GameData() {
    }
}
