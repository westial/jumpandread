package com.westial.alexa.jumpandread.domain;

public abstract class Paragraph
{
    protected String tag;
    protected String content;
    private Integer wordsCount;

    public Paragraph(String tag, String content)
    {
        this.tag = tag;
        this.content = content;
    }

    private static Integer countWords(String content)
    {
        return content.split("\\.").length;
    }

    public String getContent()
    {
        return content;
    }

    public Integer getWordsCount()
    {
        if (null == wordsCount)
        {
            wordsCount = countWords(content);
        }
        return wordsCount;
    }

    public String getTag()
    {
        return tag;
    }
}
