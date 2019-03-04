package com.westial.alexa.jumpandread.infrastructure.service.content;


import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.westial.alexa.jumpandread.domain.Configuration;
import com.westial.alexa.jumpandread.domain.content.*;
import com.westial.alexa.jumpandread.infrastructure.exception.InitializationError;
import com.westial.alexa.jumpandread.infrastructure.service.content.parser.ByPatternTextContentParser;

import java.io.IOException;
import java.util.Map;

public class ByConfigurationTextContentProviderFactory implements TextContentProviderFactory
{
    private final ContentGetterFactory contentGetterFactory;
    private final ContentGetter contentGetter;
    private TextContentParser parser;
    private ParserFactory parserFactory;
    private final AddressModifier defaultModifier;

    public ByConfigurationTextContentProviderFactory(
            ContentGetter contentGetter,
            ContentGetterFactory contentGetterFactory,
            TextContentParser defaultParser,
            ParserFactory parserFactory,
            AddressModifier defaultModifier
    )
    {
        this.contentGetterFactory = contentGetterFactory;
        this.contentGetter = contentGetter;
        this.parser = defaultParser;
        this.parserFactory = parserFactory;
        this.defaultModifier = defaultModifier;
    }

    @Override
    public TextContentProvider create(Configuration configuration)
    {
        String parserType = configuration.retrieve("PARSER_TYPE", null);
        String rawParsersByPattern = configuration.retrieve("PARSER_TYPES_BY_PATTERN", null);

        if (null != parserType && null != rawParsersByPattern)
        {
            throw new InitializationError(
                    "Only one of PARSER_TYPES_BY_PATTERN or " +
                            "PARSER_TYPE can be configured"
            );
        }

        if (null != rawParsersByPattern)
        {
            return createDependenciesByPatternProvider(rawParsersByPattern);
        }

        else if (null != parserType)
        {
            parser = parserFactory.createByType(parserType);
        }

        return new RemoteTextContentProvider(
                contentGetter,
                parser
        );
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

    private TextContentProvider createDependenciesByPatternProvider(String rawParsersByPattern)
    {
        Map<String, String> parsersByPatternConfig =
                deserializeParsersByPatternConfig(rawParsersByPattern);

        ByPatternTextContentParser byPatternParser =
                parserFactory.buildByPatternParser(
                        parsersByPatternConfig,
                        parser
                );

        ByPatternContentGetterDecorator byPatternGetter =
                contentGetterFactory.buildByPatternContentGetter(
                        parsersByPatternConfig,
                        contentGetter,
                        defaultModifier
                );

        return new ParserByPatternTextContentProvider(
                byPatternGetter,
                byPatternParser
        );
    }
}
