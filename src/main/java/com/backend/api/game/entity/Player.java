package com.backend.api.game.entity;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
public class Player {
    private String sessionId;
    private double x;
    private double y;

    public Player(String id) {
        this.sessionId = id;
        this.x = 2000;
        this.y = 1000;
    }
}
