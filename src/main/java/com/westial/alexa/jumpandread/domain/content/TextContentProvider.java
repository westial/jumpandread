package com.westial.alexa.jumpandread.domain.content;

import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;

public abstract class TextContentProvider implements ContentProvider<String, String>
{
    private LinkedList<TextContent> contents;

    @Override
    public LinkedList<Pair<String, String>> provide(
            ContentCounter counter,
            ContentAddress address,
            int startIndex,
            int itemsNumber
    ) throws EmptyContent
    {
        LinkedList<Pair<String, String>> results = new LinkedList<>();
        if (null == contents)
        {
            contents = retrieve(address);
        }
        if (contents.isEmpty())
        {
            throw new EmptyContent();
        }
        counter.sum(contents.size());
        for (int index = startIndex; index <= startIndex + itemsNumber; index ++)
        {
            if (index >= contents.size())
            {
                break;
            }
            TextContent content = contents.get(index);
            results.add(content.toPair());
        }
        return results;
    }

    @Override
    public void initCache()
    {
        contents = null;
    }

    protected abstract LinkedList<TextContent> retrieve(ContentAddress address);

}
