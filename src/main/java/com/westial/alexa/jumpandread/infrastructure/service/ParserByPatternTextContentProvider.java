package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.content.ContentAddress;
import com.westial.alexa.jumpandread.domain.content.ContentGetter;
import com.westial.alexa.jumpandread.domain.content.TextContent;
import com.westial.alexa.jumpandread.domain.content.TextContentProvider;

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

    protected LinkedList<TextContent> retrieve(ContentAddress address)
    {
        String content = getter.getContent(address);
        parser.configure(address.getUrl());
        return parser.parse(content);
    }
}
