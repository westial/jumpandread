package com.westial.alexa.jumpandread.application.exception;

public abstract class NoSearchResultException extends Exception
{
    public NoSearchResultException(String message)
    {
        super(message);
    }
}
