package com.westial.alexa.jumpandread.domain.content;

public class LinkHtmlTag extends HtmlTag
{
    public LinkHtmlTag(String text)
    {
        super(text);
    }

    public void setSrc(String src)
    {
        this.put("src", src);
    }
}
