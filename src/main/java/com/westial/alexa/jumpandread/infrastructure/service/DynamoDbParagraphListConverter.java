package com.westial.alexa.jumpandread.infrastructure.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.westial.alexa.jumpandread.domain.Paragraph;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbParagraph;

import java.util.*;

import static java.util.stream.Collectors.toMap;

public class DynamoDbParagraphListConverter implements DynamoDBTypeConverter<Map<String, Map<String, String>>, LinkedHashMap<Integer, Paragraph>>
{
    private static final String TAG_KEY = "tag";
    private static final String CONTENT_KEY = "content";
    
    @Override
    public Map<String, Map<String, String>> convert(LinkedHashMap<Integer, Paragraph> paragraphsMap)
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
    public LinkedHashMap<Integer, Paragraph> unconvert(Map<String, Map<String, String>> items)
    {
        Map<Integer, Paragraph> paragraphs = new HashMap<>();
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
        return paragraphs
                .entrySet()
                .stream()
                .sorted(Map.Entry.comparingByKey())
                .collect(
                        toMap(
                                Map.Entry::getKey,
                                Map.Entry::getValue,
                                (e1, e2) -> e2,
                                LinkedHashMap::new
                        )
                );
    }
}
