package com.westial.alexa.jumpandread.infrastructure.service;

public class SafeSearchConfiguration
{
    static boolean read(String raw)
    {
        return !raw.toLowerCase().equals("off");
    }
}
