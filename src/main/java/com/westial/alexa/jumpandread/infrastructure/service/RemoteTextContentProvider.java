package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.NoParagraphsException;
import com.westial.alexa.jumpandread.domain.content.*;

import java.util.LinkedList;


public class RemoteTextContentProvider extends TextContentProvider
{

    private final ContentGetter getter;
    private final TextContentParser parser;

    public RemoteTextContentProvider(ContentGetter getter, TextContentParser parser)
    {
        this.getter = getter;
        this.parser = parser;
    }

    protected LinkedList<TextContent> retrieve(ContentAddress address) throws EmptyContent
    {
        String content = getter.getContent(address);
        try
        {
            return parser.parse(content);
        } catch (NoParagraphsException e)
        {
            throw new EmptyContent(e.getMessage());
        }
    }
}
