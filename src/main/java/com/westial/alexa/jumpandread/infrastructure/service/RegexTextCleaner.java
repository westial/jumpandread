package com.westial.alexa.jumpandread.infrastructure.service;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTextCleaner implements TextCleaner
{
    private final Pattern pattern;

    public RegexTextCleaner(String regex)
    {
        pattern = Pattern.compile(regex);
    }

    @Override
    public String extract(String content)
    {
        Matcher matcher = pattern.matcher(content);
        if (matcher.find())
        {
            return matcher.group().trim();
        }
        return content;
    }
}
