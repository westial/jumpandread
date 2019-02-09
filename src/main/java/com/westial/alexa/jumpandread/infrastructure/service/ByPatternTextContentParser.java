package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.content.ConfigurableParser;
import com.westial.alexa.jumpandread.domain.content.TextContent;
import com.westial.alexa.jumpandread.domain.content.TextContentParser;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ByPatternTextContentParser extends TextContentParser implements ConfigurableParser
{
    private Map<Pattern, TextContentParser> parsers = new HashMap<>();
    protected TextContentParser parser;

    public ByPatternTextContentParser(TextContentParser defaultParser)
    {
        this.parser = defaultParser;
    }

    @Override
    public void addParser(String regex, TextContentParser parser)
    {
        Pattern pattern = Pattern.compile(regex);
        parsers.put(pattern, parser);
    }

    @Override
    public void configure(String target)
    {
        for (Map.Entry<Pattern, TextContentParser> entry: parsers.entrySet())
        {
            Matcher matcher = entry.getKey().matcher(target);
            if (matcher.matches())
            {
                parser = entry.getValue();
                break;
            }
        }
    }

    @Override
    public LinkedList<TextContent> parse(String content)
    {
        return parser.parse(content);
    }
}
