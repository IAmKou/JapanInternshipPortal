package com.example.jip.exception;

public class InvalidImageUrlException extends RuntimeException {

    private static final long serialVersionUID =  5003323246302489096L;

    public InvalidImageUrlException(final String message) {
        super(message);
    }
}
