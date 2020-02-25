package com.westial.alexa.jumpandread.infrastructure.structure;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.westial.alexa.jumpandread.domain.Paragraph;
import com.westial.alexa.jumpandread.infrastructure.service.content.HtmlTag;
import com.westial.alexa.jumpandread.domain.content.TextTag;

import java.util.Map;

@DynamoDBDocument
public class DynamoDbParagraph extends Paragraph
{
    private final static String TEXT_FIELD_NAME = "text";

    public DynamoDbParagraph(String tag, TextTag content)
    {
        super(tag, addTextField(content));
    }

    public DynamoDbParagraph(String tag, Map<String, String> contentMap)
    {
        super(tag, createTextTag(contentMap));
    }

    private static TextTag addTextField(TextTag content)
    {
        content.put(TEXT_FIELD_NAME, content.getText());
        return content;
    }

    private static TextTag createTextTag(Map<String, String> map)
    {
        TextTag content = new HtmlTag(map.get(TEXT_FIELD_NAME));
        content.putAll(map);
        return content;
    }
}
