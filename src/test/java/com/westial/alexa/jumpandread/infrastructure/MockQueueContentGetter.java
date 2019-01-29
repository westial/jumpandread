package com.westial.alexa.jumpandread.infrastructure;

import com.westial.alexa.jumpandread.application.exception.GettingContentException;
import com.westial.alexa.jumpandread.domain.content.ContentGetter;
import com.westial.alexa.jumpandread.domain.content.ContentAddress;

import java.util.Queue;

public class MockQueueContentGetter implements ContentGetter
{
    private Queue<String> contents;

    public MockQueueContentGetter(Queue<String> contents)
    {
        this.contents = contents;
    }

    @Override
    public String getContent(ContentAddress address) throws GettingContentException
    {
        return contents.remove();
    }
}
