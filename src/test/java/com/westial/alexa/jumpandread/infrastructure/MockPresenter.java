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
        return null;
    }

    public String strongBreak()
    {
        return null;
    }

    @Override
    public String customBreak(int milliseconds)
    {
        return null;
    }

    public String wrap(String output)
    {
        return null;
    }

    @Override
    protected String clean(String text)
    {
        return text;
    }
}
