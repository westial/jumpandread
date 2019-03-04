package com.westial.alexa.jumpandread.infrastructure.structure;

import com.westial.alexa.jumpandread.domain.content.ContentAddress;

public class SimpleContentAddress implements ContentAddress
{
    private final String url;

    public SimpleContentAddress(String url)
    {
        this.url = url;
    }

    @Override
    public String getUrl()
    {
        return url;
    }
}
