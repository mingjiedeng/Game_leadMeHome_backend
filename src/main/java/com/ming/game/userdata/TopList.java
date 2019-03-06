package com.ming.game.userdata;

import lombok.Data;

import java.util.List;

@Data
public class TopList {
    private int level;
    private List<TopScore> scores;

    public TopList(int level, List<TopScore> scores) {
        this.level = level;
        this.scores = scores;
    }
}
