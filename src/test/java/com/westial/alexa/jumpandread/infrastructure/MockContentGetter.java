package com.westial.alexa.jumpandread.infrastructure;

import com.westial.alexa.jumpandread.application.exception.GettingContentException;
import com.westial.alexa.jumpandread.domain.content.ContentGetter;
import com.westial.alexa.jumpandread.domain.content.ContentAddress;

public class MockContentGetter implements ContentGetter
{
    private final String content;

    public MockContentGetter(String forcedContent)
    {
        content = forcedContent;
    }

    public String getContent(ContentAddress address) throws GettingContentException
    {
        return content;
    }
}
