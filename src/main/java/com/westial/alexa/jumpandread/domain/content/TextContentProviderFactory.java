package com.westial.alexa.jumpandread.domain.content;

import com.westial.alexa.jumpandread.domain.Configuration;

public interface TextContentProviderFactory
{
    TextContentProvider create(Configuration configuration);
}
