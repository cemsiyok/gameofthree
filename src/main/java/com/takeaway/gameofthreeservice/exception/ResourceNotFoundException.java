package com.takeaway.gameofthreeservice.exception;

public class ResourceNotFoundException extends RuntimeException {

    public ResourceNotFoundException(String resourceName, Integer id) {
        super(String.format("%s with id not found : %d", resourceName, id));
    }

}