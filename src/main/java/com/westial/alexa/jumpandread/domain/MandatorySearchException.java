package com.westial.alexa.jumpandread.domain;

public abstract class MandatorySearchException extends Exception
{
    public MandatorySearchException(String message)
    {
        super(message);
    }
}
