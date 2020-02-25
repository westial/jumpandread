package com.westial.alexa.jumpandread.infrastructure.exception;

import com.westial.alexa.jumpandread.application.exception.NoSearchResultException;

public class EngineNoSearchResultException extends NoSearchResultException
{
    public EngineNoSearchResultException(String message)
    {
        super(message);
    }
}
