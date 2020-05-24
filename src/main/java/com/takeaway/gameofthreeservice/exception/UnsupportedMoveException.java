package com.takeaway.gameofthreeservice.exception;

public class UnsupportedMoveException extends RuntimeException {

    public UnsupportedMoveException(String message) {
        super(message);
    }
}