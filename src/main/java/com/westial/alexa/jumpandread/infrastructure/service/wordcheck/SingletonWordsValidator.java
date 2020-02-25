package com.westial.alexa.jumpandread.infrastructure.service.wordcheck;

import com.westial.alexa.jumpandread.application.service.wordcheck.WordsValidator;

public class SingletonWordsValidator implements WordsValidator
{
    private final WordsValidator validator;
    private static SingletonWordsValidator INSTANCE;

    private SingletonWordsValidator(WordsValidator validator)
    {
        this.validator = validator;
    }

    public static SingletonWordsValidator getInstance(WordsValidator validator)
    {
        if(null == INSTANCE)
        {
            INSTANCE = new SingletonWordsValidator(validator);
        }

        return INSTANCE;
    }

    @Override
    public boolean validate(String content)
    {
        return validator.validate(content);
    }
}
