package com.westial.alexa.jumpandread.application.exception;

import com.westial.alexa.jumpandread.domain.MandatorySearchException;

public class IncompleteStateMandatorySearchException extends MandatorySearchException
{
    public IncompleteStateMandatorySearchException(String message)
    {
        super(message);
    }
}
