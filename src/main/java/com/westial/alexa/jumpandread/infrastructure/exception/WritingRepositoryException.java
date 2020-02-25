package com.westial.alexa.jumpandread.infrastructure.exception;

import com.westial.alexa.jumpandread.application.exception.RepositoryException;

public class WritingRepositoryException extends RepositoryException
{
    public WritingRepositoryException(String message)
    {
        super(message);
    }
}
