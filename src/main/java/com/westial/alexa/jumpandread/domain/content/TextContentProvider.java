package com.westial.alexa.jumpandread.domain.content;

import org.apache.commons.lang3.tuple.Pair;

import java.util.LinkedList;

public abstract class TextContentProvider implements ContentProvider<Pair<String, TextTag>>
{
    private LinkedList<TextContent> contents;

    private void retrieveContents(
            ContentAddress address,
            ContentCounter counter
    ) throws EmptyContent
    {
        if (null == contents)
        {
            contents = retrieve(address);
        }
        if (contents.isEmpty())
        {
            throw new EmptyContent();
        }
        counter.sum(contents.size());
    }

    @Override
    public LinkedList<Pair<String, TextTag>> provide(ContentCounter counter, ContentAddress address) throws EmptyContent
    {
        retrieveContents(address, counter);
        return provide(counter, address, 0, contents.size());
    }

    @Override
    public LinkedList<Pair<String, TextTag>> provide(
            ContentCounter counter,
            ContentAddress address,
            int startIndex,
            int itemsNumber
    ) throws EmptyContent
    {
        retrieveContents(address, counter);
        LinkedList<Pair<String, TextTag>> results = new LinkedList<>();
        for (int index = startIndex; index < startIndex + itemsNumber; index ++)
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
    public void clean()
    {
        contents = null;
    }

    protected abstract LinkedList<TextContent> retrieve(ContentAddress address) throws EmptyContent;

}
