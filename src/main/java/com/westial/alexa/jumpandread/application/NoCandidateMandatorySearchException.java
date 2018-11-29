package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.domain.MandatorySearchException;

public class NoCandidateMandatorySearchException extends MandatorySearchException
{
    public NoCandidateMandatorySearchException(String message)
    {
        super(message);
    }
}
