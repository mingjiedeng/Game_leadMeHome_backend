package com.ming.game.userdata;

import lombok.Data;

@Data
public class Score {
    private int level;
    private int saved;
    private int seconds;
    private int star;

    public Score(int level, int saved, int seconds, int star) {
        this.level = level;
        this.saved = saved;
        this.seconds = seconds;
        this.star = star;
    }

    public Score() {
    }
}
