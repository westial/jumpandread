package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.Translator;

import java.util.List;

public class MockTranslator implements Translator
{
    @Override
    public String translate(String format, List<String> params)
    {
        return format;
    }

    @Override
    public String translate(String format)
    {
        return format;
    }
}
