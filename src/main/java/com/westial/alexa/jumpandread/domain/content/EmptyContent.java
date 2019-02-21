package com.westial.alexa.jumpandread.domain.content;

public class EmptyContent extends Exception
{
    public EmptyContent()
    {
        super();
    }

    public EmptyContent(String message)
    {
        super(message);
    }
}
