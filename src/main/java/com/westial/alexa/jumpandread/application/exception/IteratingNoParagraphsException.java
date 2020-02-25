package com.westial.alexa.jumpandread.application.exception;

import com.westial.alexa.jumpandread.domain.NoParagraphsException;

public class IteratingNoParagraphsException extends NoParagraphsException
{
    public IteratingNoParagraphsException(String message)
    {
        super(message);
    }
}
