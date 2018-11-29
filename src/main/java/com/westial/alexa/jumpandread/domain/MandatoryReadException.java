package com.westial.alexa.jumpandread.domain;

public abstract class MandatoryReadException extends RuntimeException
{
    public MandatoryReadException(String message)
    {
        super(message);
    }
}
