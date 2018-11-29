package com.westial.alexa.jumpandread.infrastructure.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.westial.alexa.jumpandread.domain.Paragraph;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbParagraph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DynamoDbParagraphListConverter implements DynamoDBTypeConverter<List<Map<String, String>>, List<Paragraph>>
{
    private static final String TAG_KEY = "tag";
    private static final String CONTENT_KEY = "content";
    
    @Override
    public List<Map<String, String>> convert(List<Paragraph> paragraphs)
    {
        List<Map<String, String>> items = new ArrayList<>();
        Map<String, String> item;
        for (Paragraph paragraph : paragraphs)
        {
            item = new HashMap<>();
            item.put(TAG_KEY, paragraph.getTag());
            item.put(CONTENT_KEY, paragraph.getContent());
            items.add(item);
        }
        return items;
    }

    @Override
    public List<Paragraph> unconvert(List<Map<String, String>> items)
    {
        List<Paragraph> paragraphs = new ArrayList<>();
        for (Map<String, String> item : items)
        {
            paragraphs.add(
                    new DynamoDbParagraph(
                            item.get(TAG_KEY),
                            item.get(CONTENT_KEY)
                    )
            );
        }
        return paragraphs;
    }
}
