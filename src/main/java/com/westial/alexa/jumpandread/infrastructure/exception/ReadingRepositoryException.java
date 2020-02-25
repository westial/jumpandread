package com.westial.alexa.jumpandread.infrastructure.exception;

import com.westial.alexa.jumpandread.application.exception.RepositoryException;

public class ReadingRepositoryException extends RepositoryException
{
    public ReadingRepositoryException(String message)
    {
        super(message);
    }
}
