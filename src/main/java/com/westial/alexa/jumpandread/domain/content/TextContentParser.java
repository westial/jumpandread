package com.westial.alexa.jumpandread.domain.content;

import com.westial.alexa.jumpandread.domain.NoParagraphsException;

import java.util.LinkedList;

/**
 * Parse the given content and returns a list of text type contents or a
 */
public abstract class TextContentParser implements ContentParser<TextContent>
{
    public abstract LinkedList<TextContent> parse(String content) throws NoParagraphsException;
}
