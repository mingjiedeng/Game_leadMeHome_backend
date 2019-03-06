package com.ming.game.gamedata;

import lombok.Data;

@Data
public class Hole {
    private String type;
    private int x;
    private int y;
    private int radius;
    private int targetX;
    private int targetY;
}
