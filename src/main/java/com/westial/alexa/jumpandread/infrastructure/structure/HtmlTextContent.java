package com.westial.alexa.jumpandread.infrastructure.structure;

import com.westial.alexa.jumpandread.infrastructure.service.content.HtmlTag;
import com.westial.alexa.jumpandread.domain.content.TextContent;

public class HtmlTextContent extends TextContent
{
    public HtmlTextContent(String label, HtmlTag tag)
    {
        super(label, tag);
    }
}
