package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.content.*;

import java.util.LinkedList;


public class MockTextContentProvider extends TextContentProvider
{

    private final ContentGetter getter;
    private final TextContentParser parser;

    public MockTextContentProvider(ContentGetter getter, TextContentParser parser)
    {
        this.getter = getter;
        this.parser = parser;
    }

    protected LinkedList<TextContent> retrieve(ContentAddress address)
    {
        String content = getter.getContent(address);
        return parser.parse(content);
    }
}
