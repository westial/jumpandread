package com.westial.alexa.jumpandread.infrastructure.service;

import java.util.List;

public class RandomDuckDuckGoLocaleProvider implements DuckDuckGoLocaleProvider
{
    private final List<String> locales;

    public RandomDuckDuckGoLocaleProvider(String filePath)
    {
        this.locales = FileSystemService.readListFromFile(filePath);
    }

    @Override
    public String provide()
    {
        return RandomService.randomChoice(locales);
    }
}
