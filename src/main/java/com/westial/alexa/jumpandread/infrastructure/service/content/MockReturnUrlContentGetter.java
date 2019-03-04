package com.westial.alexa.jumpandread.infrastructure.service.content;

import com.westial.alexa.jumpandread.application.exception.GettingContentException;
import com.westial.alexa.jumpandread.domain.content.ContentAddress;
import com.westial.alexa.jumpandread.domain.content.ContentGetter;

public class MockReturnUrlContentGetter implements ContentGetter
{
    @Override
    public String getContent(ContentAddress address) throws GettingContentException
    {
        return address.getUrl();
    }
}
