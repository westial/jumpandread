package com.westial.alexa.jumpandread.domain;

import java.util.List;

public interface Translator
{
    String translate(String format, List<String> params);

    String translate(String format);
}
