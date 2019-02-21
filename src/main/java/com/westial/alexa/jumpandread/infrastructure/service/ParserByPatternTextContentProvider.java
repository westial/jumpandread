package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.NoParagraphsException;
import com.westial.alexa.jumpandread.domain.content.*;

import java.util.LinkedList;


public class ParserByPatternTextContentProvider extends TextContentProvider
{

    private final ContentGetter getter;
    private final ByPatternTextContentParser parser;

    public ParserByPatternTextContentProvider(
            ContentGetter getter,
            ByPatternTextContentParser parser
    )
    {
        this.getter = getter;
        this.parser = parser;
    }

    protected LinkedList<TextContent> retrieve(ContentAddress address) throws EmptyContent
    {
        String content = getter.getContent(address);
        parser.configure(address.getUrl());
        try
        {
            return parser.parse(content);
        } catch (NoParagraphsException e)
        {
            throw new EmptyContent(e.getMessage());
        }
    }
}
