package com.westial.alexa.jumpandread.domain;

import com.westial.alexa.jumpandread.domain.content.TextTag;

public abstract class Paragraph
{
    protected String tag;
    protected TextTag content;
    private Integer wordsCount;

    public Paragraph(String tag, TextTag content)
    {
        this.tag = tag;
        this.content = content;
        wordsCount = countWords(content.getText());
    }

    private static Integer countWords(String content)
    {
        return content.split("\\s").length;
    }

    public TextTag getContent()
    {
        return content;
    }

    public Integer getWordsCount()
    {
        return wordsCount;
    }

    public String getTag()
    {
        return tag;
    }
}
