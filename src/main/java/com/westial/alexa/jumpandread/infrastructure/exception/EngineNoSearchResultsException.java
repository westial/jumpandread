package com.westial.alexa.jumpandread.infrastructure.exception;

import com.westial.alexa.jumpandread.application.exception.NoSearchResultsException;

public class EngineNoSearchResultsException extends NoSearchResultsException
{
    public EngineNoSearchResultsException(String message)
    {
        super(message);
    }
}
