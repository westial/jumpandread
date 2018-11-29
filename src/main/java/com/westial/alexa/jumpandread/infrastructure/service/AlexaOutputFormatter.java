package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.OutputFormatter;
import org.apache.commons.text.StringEscapeUtils;

public class AlexaOutputFormatter extends OutputFormatter
{
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

    private String addPauses(String content)
    {
        // Important to replace forced token pauses before next ones
        content = content.replace(OutputFormatter.STRONG_TOKEN, strongBreak());
        content = content.replace(OutputFormatter.WEAK_TOKEN, weakBreak());

        // Others
        content = content.replace(". ", strongBreak());
        content = content.replace(", ", weakBreak());
        return content;
    }

    private String clean(String content)
    {
        content = StringEscapeUtils.escapeXml11(content);
        content = content.replace("\\[\"\\']", "");
        return content;
    }

    public String envelop(String output)
    {
        output = clean(output);
        output = addPauses(output);
        System.out.printf("DEBUG: Output: %s\n", output);
        return String.format("<speak>%s</speak>", output);
    }
}
