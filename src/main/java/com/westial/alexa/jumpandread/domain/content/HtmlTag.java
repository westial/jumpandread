package com.westial.alexa.jumpandread.domain.content;

public class HtmlTag extends TextTag
{
    public HtmlTag(String text)
    {
        super(text);
    }

    public void setDescription(String description)
    {
        this.put("description", description);
    }
}
