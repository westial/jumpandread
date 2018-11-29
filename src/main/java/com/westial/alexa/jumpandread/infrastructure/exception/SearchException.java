package com.westial.alexa.jumpandread.infrastructure.exception;

public abstract class SearchException extends RuntimeException
{
    public SearchException(String message)
    {
        super(message);
    }
}
