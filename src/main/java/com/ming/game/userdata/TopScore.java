package com.ming.game.userdata;

import lombok.Data;

@Data
public class TopScore {
    private String name;
    private int saved;
    private int seconds;

    public TopScore(String name, int saved, int seconds) {
        this.name = name;
        this.saved = saved;
        this.seconds = seconds;
    }
}
