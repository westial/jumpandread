package com.westial.alexa.jumpandread.domain.content;

import java.util.LinkedList;

public abstract class TextContentParser implements ContentParser<TextContent>
{
    public abstract LinkedList<TextContent> parse(String content);
}
