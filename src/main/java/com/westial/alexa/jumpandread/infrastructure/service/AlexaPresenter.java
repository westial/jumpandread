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
        return "<break strength=\"strong\"/>";
    }

    public String strongBreak()
    {
        return "<break time=\"800ms\"/>";
    }

    @Override
    public String whisper(boolean startToggle)
    {
        if (startToggle)
        {
            return "<amazon:effect name=\"whispered\">";
        }
        else
        {
            return "</amazon:effect>";
        }
    }

    @Override
    public String domain(boolean startToggle)
    {
        if (startToggle)
        {
            return "<say-as interpret-as=\"spell-out\">";
        }
        else
        {
            return "</say-as>";
        }
    }

    @Override
    public String alternativeVoice(boolean startToggle)
    {
        if (startToggle)
        {
            return String.format(
                    "<voice name=\"%s\">",
                    translator.translate("configuration.alternative.voice")
            );
        }
        else
        {
            return "</voice>";
        }
    }

    @Override
    public String emphasis(boolean startToggle)
    {
        if (startToggle)
        {
            return "<emphasis level=\"strong\">";
        }
        else
        {
            return "<emphasis>";
        }
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
