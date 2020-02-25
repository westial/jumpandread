package com.westial.alexa.jumpandread.domain.content;

public interface ConfigurableParser
{
    void addParser(String regex, TextContentParser parser);

    void configure(String target);
}
