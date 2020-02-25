package com.westial.alexa.jumpandread.application.exception;

public abstract class RepositoryException extends RuntimeException
{
    public RepositoryException(String message)
    {
        super(message);
    }
}
