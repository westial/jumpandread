package com.westial.alexa.jumpandread.infrastructure;

import com.westial.alexa.jumpandread.application.exception.GettingContentException;
import com.westial.alexa.jumpandread.domain.content.ContentGetter;
import com.westial.alexa.jumpandread.domain.content.ContentAddress;

public class ExceptionContentGetter implements ContentGetter
{
    private final String message;

    public ExceptionContentGetter(String message)
    {
        this.message = message;
    }

    public String getContent(ContentAddress address)
    {
        throw new GettingContentException(message);
    }
}
