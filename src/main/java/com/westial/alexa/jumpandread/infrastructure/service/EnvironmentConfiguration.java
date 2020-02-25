package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.Configuration;
import com.westial.alexa.jumpandread.infrastructure.exception.ValueNotFoundException;

import java.util.HashMap;
import java.util.Map;

public class EnvironmentConfiguration implements Configuration
{
    private Map<String, String> cache;

    @Override
    public void register(String name, String value)
    {
        initializeCache();

        cache.put(name, value);
    }

    @Override
    public String retrieve(String name)
    {
        initializeCache();

        if (!cache.containsKey(name))
        {
            throw new ValueNotFoundException(
                    String.format(
                            "The required configuration value \"%s\" has not been initialized",
                            name
                    )
            );
        }

        return cache.get(name);
    }

    @Override
    public String retrieve(String name, String defaultValue)
    {
        try
        {
            return retrieve(name);
        } catch (ValueNotFoundException e)
        {
            return defaultValue;
        }
    }

    private void initializeCache()
    {
        if (cache == null)
        {
            cache = new HashMap<>();
            Map<String, String> sysEnv = System.getenv();
            for (String name : sysEnv.keySet())
            {
                cache.put(name, sysEnv.get(name));
            }
        }
    }
}
