package com.westial.alexa.jumpandread.infrastructure;

import com.westial.alexa.jumpandread.domain.content.TextContent;
import com.westial.alexa.jumpandread.domain.content.TextContentParser;
import utils.RandomContent;

import java.util.LinkedList;

public class MockContentParser extends TextContentParser
{
    public LinkedList<TextContent> parse(String content)
    {
        return new LinkedList<>(RandomContent.createContents(3, 6));
    }
}
