package com.westial.alexa.jumpandread.infrastructure;

import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.domain.Translator;

public class MockPresenter extends Presenter
{
    public MockPresenter(Translator translator)
    {
        super(translator);
    }

    public String weakBreak()
    {
        return "{WEAK_TOKEN}";
    }

    public String strongBreak()
    {
        return "{STRONG_TOKEN}";
    }

    @Override
    public String whisper(boolean startToggle)
    {
        if (startToggle)
        {
            return "{WHISPER_START_TOKEN}";
        }
        else
        {
            return "{WHISPER_END_TOKEN}";
        }
    }

    @Override
    public String emphasis(boolean startToggle)
    {
        if (startToggle)
        {
            return "{EMPHASIS_START_TOKEN}";
        }
        else
        {
            return "{EMPHASIS_END_TOKEN}";
        }
    }

    public String wrap(String output)
    {
        return output;
    }

    @Override
    protected String clean(String text)
    {
        return text;
    }
}
