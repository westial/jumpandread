package com.westial.alexa.jumpandread.domain;

public abstract class MandatorySearchException extends RuntimeException
{
    public MandatorySearchException(String message)
    {
        super(message);
    }
}
