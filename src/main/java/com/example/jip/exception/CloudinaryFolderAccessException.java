package com.example.jip.exception;

public class CloudinaryFolderAccessException extends RuntimeException {

    private static final long serialVersionUID =  5003323242302480096L;

    public CloudinaryFolderAccessException(final String message, Exception exception) {
        super(message + exception);
    }
}
