package com.westial.alexa.jumpandread.domain;

public interface Configuration
{
    void register(String name, String value);

    String retrieve(String name);

    String retrieve(String name, String defaultValue);
}
