package com.westial.alexa.jumpandread.infrastructure.exception;

public class InitializationError extends RuntimeException
{
    public InitializationError(String message)
    {
        super(message);
    }
}
