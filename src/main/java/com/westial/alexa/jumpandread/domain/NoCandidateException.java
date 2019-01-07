package com.westial.alexa.jumpandread.domain;

public abstract class NoCandidateException extends RuntimeException
{
    public NoCandidateException(String message)
    {
        super(message);
    }
}
