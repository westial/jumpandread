package com.westial.alexa.jumpandread.infrastructure;

import com.westial.alexa.jumpandread.domain.content.ContentAddress;

public class MockContentAddress implements ContentAddress
{
    private final String url;

    public MockContentAddress(String url)
    {
        this.url = url;
    }

    @Override
    public String getUrl()
    {
        return url;
    }
}
