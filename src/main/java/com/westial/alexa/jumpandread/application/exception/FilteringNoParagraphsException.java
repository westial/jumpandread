package com.westial.alexa.jumpandread.application.exception;

import com.westial.alexa.jumpandread.domain.NoParagraphsException;

public class FilteringNoParagraphsException extends NoParagraphsException
{
    public FilteringNoParagraphsException(String message)
    {
        super(message);
    }
}
