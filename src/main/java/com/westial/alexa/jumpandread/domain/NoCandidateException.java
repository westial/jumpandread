package com.westial.alexa.jumpandread.domain;

public abstract class NoCandidateException extends Exception
{
    public NoCandidateException(String message)
    {
        super(message);
    }
}
