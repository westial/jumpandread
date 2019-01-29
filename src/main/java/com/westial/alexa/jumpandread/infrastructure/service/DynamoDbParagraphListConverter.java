package com.westial.alexa.jumpandread.infrastructure.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.westial.alexa.jumpandread.domain.Paragraph;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbParagraph;

import java.util.*;

public class DynamoDbParagraphListConverter implements DynamoDBTypeConverter<Map<Integer, Map<String, String>>, LinkedHashMap<Integer, Paragraph>>
{
    private static final String TAG_KEY = "tag";
    private static final String CONTENT_KEY = "content";
    
    @Override
    public Map<Integer, Map<String, String>> convert(LinkedHashMap<Integer, Paragraph> paragraphsMap)
    {
        Map<Integer, Map<String, String>> items = new HashMap<>();
        Map<String, String> item;
        for (Map.Entry<Integer, Paragraph> entry : paragraphsMap.entrySet())
        {
            item = new HashMap<>();
            item.put(TAG_KEY, entry.getValue().getTag());
            item.put(CONTENT_KEY, entry.getValue().getContent());
            items.put(entry.getKey(), item);
        }
        return items;
    }

    @Override
    public LinkedHashMap<Integer, Paragraph> unconvert(Map<Integer, Map<String, String>> items)
    {
        LinkedHashMap<Integer, Paragraph> paragraphs = new LinkedHashMap<>();
        for (Map.Entry<Integer, Map<String, String>> entry : items.entrySet())
        {
            paragraphs.put(
                    entry.getKey(),
                    new DynamoDbParagraph(
                            entry.getValue().get(TAG_KEY),
                            entry.getValue().get(CONTENT_KEY)
                    )
            );
        }
        return paragraphs;
    }
}
