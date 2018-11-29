package com.westial.alexa.jumpandread.infrastructure.exception;

public class ValueNotFoundException extends RuntimeException
{
    public ValueNotFoundException()
    {
    }

    public ValueNotFoundException(String s)
    {
        super(s);
    }

    public ValueNotFoundException(String s, Throwable throwable)
    {
        super(s, throwable);
    }

    public ValueNotFoundException(Throwable throwable)
    {
        super(throwable);
    }

    public ValueNotFoundException(String s, Throwable throwable, boolean b, boolean b1)
    {
        super(s, throwable, b, b1);
    }
}
