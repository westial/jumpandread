package com.westial.alexa.jumpandread.domain.content;

import java.util.LinkedList;

interface ContentParser<C extends Content>
{
    LinkedList<C> parse(String content);
}
