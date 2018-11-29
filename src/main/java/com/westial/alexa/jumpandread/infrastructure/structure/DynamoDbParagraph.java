package com.westial.alexa.jumpandread.infrastructure.structure;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBDocument;
import com.westial.alexa.jumpandread.domain.Paragraph;

@DynamoDBDocument
public class DynamoDbParagraph extends Paragraph
{
    public DynamoDbParagraph(String tag, String content)
    {
        super(tag, content);
    }

    @Override
    public String getContent()
    {
        return super.getContent();
    }

    @DynamoDBAttribute(attributeName = "content")
    public void setContent(String content)
    {
        super.content = content;
    }

    @Override
    public String getTag()
    {
        return super.getTag();
    }

    @DynamoDBAttribute(attributeName = "tag")
    public void setTag(String tag)
    {
        super.tag = tag;
    }
}
