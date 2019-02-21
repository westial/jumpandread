package com.westial.alexa.jumpandread.domain.content;

import java.util.LinkedList;

interface ContentProvider<T>
{
    LinkedList<T> provide(
            ContentCounter counter,
            ContentAddress address,
            int startIndex,
            int itemsNumber
    ) throws EmptyContent;

    LinkedList<T> provide(
            ContentCounter counter,
            ContentAddress address
    ) throws EmptyContent;

    void clean();
}
