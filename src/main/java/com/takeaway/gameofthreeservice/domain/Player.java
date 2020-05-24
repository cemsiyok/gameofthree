package com.takeaway.gameofthreeservice.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
public class Player {

    private Integer id;
    private String name;

    public Player(String name) {
        this.name = name;
    }

    public boolean isPersisted() {
        return id != null;
    }
}