package com.westial.alexa.jumpandread.infrastructure.service.content;

import com.westial.alexa.jumpandread.domain.content.TextContentParser;
import com.westial.alexa.jumpandread.infrastructure.exception.InitializationError;
import com.westial.alexa.jumpandread.infrastructure.service.content.parser.ByPatternTextContentParser;
import com.westial.alexa.jumpandread.infrastructure.service.content.parser.MediumTextContentParser;
import com.westial.alexa.jumpandread.infrastructure.service.content.parser.WebNarrativeTextContentParser;
import com.westial.alexa.jumpandread.infrastructure.service.content.parser.WebSearchTextContentParser;
import com.westial.alexa.jumpandread.infrastructure.structure.ParserType;

import java.util.Map;

public class ParserFactory
{
    private final String mediumFilterRegex;
    private final String uriRoot;

    public ParserFactory(String mediumFilterRegex, String uriRoot)
    {
        this.mediumFilterRegex = mediumFilterRegex;
        this.uriRoot = uriRoot;
    }

    TextContentParser createByType(String parserType)
    {
        switch (ParserType.valueOf(parserType))
        {
            case WebSearch:
                return new WebSearchTextContentParser();

            case WebNarrative:
                return new WebNarrativeTextContentParser();

            case CustomMedium:
                return new MediumTextContentParser(mediumFilterRegex, uriRoot);

            default:
                throw new InitializationError(
                        String.format(
                                "Missing appropriate content parser for " +
                                        "configuration PARSER_TYPE as %s",
                                parserType
                        )
                );
        }
    }

    public ByPatternTextContentParser buildByPatternParser(
            Map<String, String> parsersByPatternConfig,
            TextContentParser defaultParser
    )
    {
        ByPatternTextContentParser byPatternParser =
                new ByPatternTextContentParser(defaultParser);

        for (Map.Entry<String, String> entry: parsersByPatternConfig.entrySet())
        {
            byPatternParser.addParser(
                    entry.getKey(),
                    createByType(entry.getValue())
            );
        }

        return byPatternParser;
    }
}
