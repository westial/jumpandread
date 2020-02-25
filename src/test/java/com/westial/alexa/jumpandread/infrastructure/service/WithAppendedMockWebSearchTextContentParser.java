package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.NoParagraphsException;
import com.westial.alexa.jumpandread.domain.content.TextContent;
import com.westial.alexa.jumpandread.infrastructure.service.content.parser.WebSearchTextContentParser;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

public class WithAppendedMockWebSearchTextContentParser extends WebSearchTextContentParser
{
    private final List<TextContent> appendings;

    public WithAppendedMockWebSearchTextContentParser(TextContent... appendArgs)
    {
        this.appendings = new LinkedList<>(Arrays.asList(appendArgs));
    }

    @Override
    public LinkedList<TextContent> parse(String content) throws NoParagraphsException
    {
        LinkedList<TextContent> contents = super.parse(content);
        contents.addAll(appendings);
        return contents;
    }
}
