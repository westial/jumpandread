package com.westial.alexa.jumpandread.infrastructure.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTypeConverter;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.westial.alexa.jumpandread.domain.Paragraph;
import com.westial.alexa.jumpandread.infrastructure.exception.ReadingRepositoryException;
import com.westial.alexa.jumpandread.infrastructure.exception.WritingRepositoryException;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbParagraph;

import java.io.IOException;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

import static java.util.stream.Collectors.toMap;

public class DynamoDbParagraphListConverter implements DynamoDBTypeConverter<Map<String, Map<String, String>>, LinkedHashMap<Integer, Paragraph>>
{
    private static final String TAG_KEY = "tag";
    private static final String CONTENT_KEY = "content";
    private final ObjectMapper mapper;

    public DynamoDbParagraphListConverter()
    {
        mapper = new ObjectMapper();
    }

    @Override
    public Map<String, Map<String, String>> convert(LinkedHashMap<Integer, Paragraph> paragraphsMap)
    {
        Map<String, Map<String, String>> items = new HashMap<>();
        Map<String, String> item;
        for (Map.Entry<Integer, Paragraph> entry : paragraphsMap.entrySet())
        {
            String content;
            try
            {
                content = mapper.writeValueAsString(entry.getValue().getContent());
            } catch (JsonProcessingException e)
            {
                throw new ReadingRepositoryException(
                        String.format(
                                "Unexpected paragraph content property format. %s",
                                e.getMessage()
                        )
                );
            }

            item = new HashMap<>();
            item.put(TAG_KEY, entry.getValue().getTag());
            item.put(CONTENT_KEY, content);
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
            Map<String, String> contentMap;
            try
            {
                contentMap = mapper.readValue(
                        entry.getValue().get(CONTENT_KEY),
                        new TypeReference<Map<String, String>>()
                        {
                        }
                );
            } catch (IOException e)
            {
                throw new WritingRepositoryException(
                        String.format(
                                "Unexpected paragraph content property format. %s",
                                e.getMessage()
                        )
                );
            }
            paragraphs.put(
                    Integer.parseInt(entry.getKey()),
                    new DynamoDbParagraph(
                            entry.getValue().get(TAG_KEY),
                            contentMap
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
