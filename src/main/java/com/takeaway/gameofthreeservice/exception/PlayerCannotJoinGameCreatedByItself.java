package com.takeaway.gameofthreeservice.exception;

public class PlayerCannotJoinGameCreatedByItself extends RuntimeException {

    public PlayerCannotJoinGameCreatedByItself() {
        super("Player cannot join the game created by itself");
    }
}