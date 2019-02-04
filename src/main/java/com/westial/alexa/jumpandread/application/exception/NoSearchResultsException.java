package com.westial.alexa.jumpandread.application.exception;

public abstract class NoSearchResultsException extends Exception {
    public NoSearchResultsException(String message)
    {
        super(message);
    }
}
