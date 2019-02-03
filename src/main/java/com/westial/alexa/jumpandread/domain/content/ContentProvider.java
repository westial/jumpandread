package com.westial.alexa.jumpandread.domain.content;

import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;

interface ContentProvider<K, C>
{
    LinkedList<Pair<K, C>> provide(
            ContentCounter counter,
            ContentAddress address,
            int startIndex,
            int itemsNumber
    ) throws EmptyContent;

    void clean();
}
