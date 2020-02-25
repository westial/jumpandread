package com.westial.alexa.jumpandread.domain.content;

import com.westial.alexa.jumpandread.application.exception.GettingContentException;

public interface ContentGetter
{
    String getContent(ContentAddress address) throws GettingContentException;
}
