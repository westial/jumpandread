package com.westial.alexa.jumpandread.application.exception;

import com.westial.alexa.jumpandread.domain.MandatoryReadException;

public class CannotContinueMandatoryReadException extends MandatoryReadException
{
    public CannotContinueMandatoryReadException(String message)
    {
        super(message);
    }
}
