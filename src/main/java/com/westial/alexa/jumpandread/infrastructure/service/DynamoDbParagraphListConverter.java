package com.westial.alexa.jumpandread.infrastructure.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.westial.alexa.jumpandread.domain.Paragraph;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbParagraph;

import java.util.*;

public class DynamoDbParagraphListConverter implements DynamoDBTypeConverter<Map<String, Map<String, String>>, Map<Integer, Paragraph>>
{
    private static final String TAG_KEY = "tag";
    private static final String CONTENT_KEY = "content";
    
    @Override
    public Map<String, Map<String, String>> convert(Map<Integer, Paragraph> paragraphsMap)
    {
        Map<String, Map<String, String>> items = new HashMap<>();
        Map<String, String> item;
        for (Map.Entry<Integer, Paragraph> entry : paragraphsMap.entrySet())
        {
            item = new HashMap<>();
            item.put(TAG_KEY, entry.getValue().getTag());
            item.put(CONTENT_KEY, entry.getValue().getContent());
            items.put(String.valueOf(entry.getKey()), item);
        }
        return items;
    }

    @Override
    public Map<Integer, Paragraph> unconvert(Map<String, Map<String, String>> items)
    {
        Map<Integer, Paragraph> paragraphs = new LinkedHashMap<>();
        for (Map.Entry<String, Map<String, String>> entry : items.entrySet())
        {
            paragraphs.put(
                    Integer.parseInt(entry.getKey()),
                    new DynamoDbParagraph(
                            entry.getValue().get(TAG_KEY),
                            entry.getValue().get(CONTENT_KEY)
                    )
            );
        }
        return paragraphs;
    }
}
