package com.westial.alexa.jumpandread.application.exception;

import com.westial.alexa.jumpandread.domain.NoCandidateException;

public class IteratingNoCandidateException extends NoCandidateException
{
    public IteratingNoCandidateException(String message)
    {
        super(message);
    }
}
