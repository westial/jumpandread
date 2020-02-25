package com.westial.alexa.jumpandread.infrastructure.service.content;

import com.westial.alexa.jumpandread.domain.LinkToCandidateDescriptor;
import com.westial.alexa.jumpandread.domain.content.TextTag;

public class LinkHtmlTag extends HtmlTag implements LinkToCandidateDescriptor
{
    public LinkHtmlTag(String text)
    {
        super(text);
    }

    public LinkHtmlTag(TextTag htmlTag)
    {
        super(htmlTag.getText());
        this.setSrc(htmlTag.get("src"));
        this.setDescription(htmlTag.get("description"));
    }

    public void setSrc(String src)
    {
        this.put("src", src);
    }

    @Override
    public String getSrc()
    {
        return this.get("src");
    }

    @Override
    public String getDescription()
    {
        return this.get("description");
    }
}
