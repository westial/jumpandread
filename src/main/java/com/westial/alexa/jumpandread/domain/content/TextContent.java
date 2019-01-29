package com.westial.alexa.jumpandread.domain.content;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public abstract class TextContent implements Content<String, String>
{
    private final String label;
    private final String content;

    public TextContent(String label, String content)
    {
        this.label = label;
        this.content = content;
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    @Override
    public String getContent()
    {
        return content;
    }

    @Override
    public Pair<String, String> toPair()
    {
        return new ImmutablePair<>(getLabel(), getContent());
    }
}
