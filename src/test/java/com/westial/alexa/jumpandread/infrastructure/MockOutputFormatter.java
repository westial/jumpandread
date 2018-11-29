package com.westial.alexa.jumpandread.infrastructure;

import com.westial.alexa.jumpandread.domain.OutputFormatter;

public class MockOutputFormatter extends OutputFormatter
{
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

    public String envelop(String output)
    {
        return null;
    }
}
