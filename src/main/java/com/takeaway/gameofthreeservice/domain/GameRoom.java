package com.takeaway.gameofthreeservice.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;
import java.util.LinkedList;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Data
public class GameRoom {

    private Integer id;
    private Integer firstPlayerId;
    private Integer secondPlayerId;
    private LinkedList<Move> moves;
    private Integer currentNumber;
    private GameState state;
    private OffsetDateTime createdDate;

    public boolean isPersisted() {
        return id != null;
    }
}