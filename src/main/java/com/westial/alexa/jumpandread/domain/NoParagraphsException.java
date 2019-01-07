package com.westial.alexa.jumpandread.domain;

public abstract class NoParagraphsException extends RuntimeException
{
    public NoParagraphsException(String message)
    {
        super(message);
    }
}
