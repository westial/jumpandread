package com.westial.alexa.jumpandread.infrastructure.service.wordcheck;

import com.westial.alexa.jumpandread.application.service.wordcheck.FileToWordList;

import java.io.IOException;
import java.util.List;

public class SingletonFileToWordList implements FileToWordList
{
    private final List<String> cached;
    private static SingletonFileToWordList INSTANCE;

    private SingletonFileToWordList(FileToWordList converter) throws IOException
    {
        this.cached = converter.convert();
    }

    public static SingletonFileToWordList getInstance(FileToWordList converter) throws IOException
    {
        if(null == INSTANCE)
        {
            INSTANCE = new SingletonFileToWordList(converter);
        }

        return INSTANCE;
    }

    @Override
    public List<String> convert()
    {
        return cached;
    }
}
