package com.westial.alexa.jumpandread.infrastructure.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.westial.alexa.jumpandread.domain.content.TextContentParser;
import com.westial.alexa.jumpandread.infrastructure.exception.InitializationError;
import com.westial.alexa.jumpandread.infrastructure.structure.ParserType;

import java.io.IOException;
import java.util.Map;

public class ParserFactory
{
    public static TextContentParser createParserByType(String parserType)
    {
        switch (ParserType.valueOf(parserType))
        {
            case WebSearch:
                return new WebSearchTextContentParser();

            case WebNarrative:
                return new WebNarrativeTextContentParser();

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

    private static Map<String, String> deserializeParsersByPatternConfig(String rawParsersByPattern)
    {
        ObjectMapper mapper = new ObjectMapper();
        try
        {
            return mapper.readValue(
                    rawParsersByPattern,
                    new TypeReference<Map<String, String>>()
                    {
                    }
            );
        } catch (IOException e)
        {
            throw new InitializationError(
                    String.format(
                            "%s. Bad formatted configuration json for " +
                                    "PARSER_TYPES_BY_PATTERN as " +
                                    "%s",
                            e.getMessage(),
                            rawParsersByPattern
                    )
            );
        }
    }

    public static ByPatternTextContentParser createByPatternParser(
            String rawParsersByPattern,
            TextContentParser defaultParser
    )
    {
        Map<String, String> parsersByPatternConfig =
                deserializeParsersByPatternConfig(rawParsersByPattern);

        ByPatternTextContentParser byPatternParser =
                new ByPatternTextContentParser(defaultParser);

        for (Map.Entry<String, String> entry: parsersByPatternConfig.entrySet())
        {
            byPatternParser.addParser(
                    entry.getKey(),
                    createParserByType(entry.getValue())
            );
        }

        return byPatternParser;
    }
}
