package com.westial.alexa.jumpandread.domain.content;

import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

public abstract class TextContent implements Content<String, TextTag>
{
    private final String label;
    private final TextTag tag;

    public TextContent(String label, TextTag tag)
    {
        this.label = label;
        this.tag = tag;
    }

    @Override
    public String getLabel()
    {
        return label;
    }

    @Override
    public TextTag getTag()
    {
        return tag;
    }

    @Override
    public Pair<String, TextTag> toPair()
    {
        return new ImmutablePair<>(getLabel(), getTag());
    }
}
