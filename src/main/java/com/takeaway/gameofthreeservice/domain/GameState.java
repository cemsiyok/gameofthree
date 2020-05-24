package com.takeaway.gameofthreeservice.domain;

public enum GameState {

    PENDING_SECOND_PLAYER_TO_JOIN,
    BEING_PLAYED,
    FINISHED;

    public boolean isMoveAcceptable() {
        return this == BEING_PLAYED;
    }
}