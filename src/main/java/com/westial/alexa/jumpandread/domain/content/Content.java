package com.westial.alexa.jumpandread.domain.content;

import org.apache.commons.lang3.tuple.Pair;

interface Content<K, C>
{
    K getLabel();

    C getTag();

    Pair<K, C> toPair();
}
