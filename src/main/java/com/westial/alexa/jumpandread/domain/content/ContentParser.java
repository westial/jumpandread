package com.westial.alexa.jumpandread.domain.content;

import com.westial.alexa.jumpandread.domain.NoParagraphsException;

import java.util.LinkedList;

interface ContentParser<C extends Content>
{
    LinkedList<C> parse(String content) throws NoParagraphsException;
}
