package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.domain.Translator;
import org.apache.commons.text.StringEscapeUtils;

public class AlexaPresenter extends Presenter
{
    public AlexaPresenter(Translator translator)
    {
        super(translator);
    }

    public String weakBreak()
    {
        return "<break strength=\"x-strong\"/>";
    }

    public String strongBreak()
    {
        return "<break time=\"1500ms\"/>";
    }

    public String customBreak(int milliseconds)
    {
        return String.format("<break time=\"%dms\"/>", milliseconds);
    }

    protected String clean(String content)
    {
        content = StringEscapeUtils.escapeXml11(content);
        content = content.replace("\\[\"\\']", "");
        return content;
    }

    protected String wrap(String output)
    {
        output = String.format("<speak>%s</speak>", output);
        System.out.printf("DEBUG: Wrapped output: %s\n", output);
        return output;
    }
}
