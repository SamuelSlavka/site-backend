package com.backend.api.game.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Board {
    private double xmin;
    private double xmax;
    private double ymin;
    private double ymax;

    public Board() {
        this.xmin = 1000;
        this.xmax = 3000;
        this.ymin = 200;
        this.ymax = 1800;
    }
}
