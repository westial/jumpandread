package com.westial.alexa.jumpandread.infrastructure.service;


import com.westial.alexa.jumpandread.domain.Configuration;
import com.westial.alexa.jumpandread.domain.content.ContentGetter;
import com.westial.alexa.jumpandread.domain.content.TextContentParser;
import com.westial.alexa.jumpandread.domain.content.TextContentProvider;
import com.westial.alexa.jumpandread.domain.content.TextContentProviderFactory;
import com.westial.alexa.jumpandread.infrastructure.exception.InitializationError;

public class ByConfigurationTextContentProviderFactory implements TextContentProviderFactory
{
    private final ContentGetter contentGetter;
    private TextContentParser parser;

    public ByConfigurationTextContentProviderFactory(
            ContentGetter contentGetter,
            TextContentParser defaultParser
    )
    {
        this.contentGetter = contentGetter;
        this.parser = defaultParser;
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
            return createParsersByPatternProvider(rawParsersByPattern);
        }

        else if (null != parserType)
        {
            parser = ParserFactory.createParserByType(parserType);
        }

        return new RemoteTextContentProvider(
                contentGetter,
                parser
        );
    }

    private TextContentProvider createParsersByPatternProvider(String rawParsersByPattern)
    {
        ByPatternTextContentParser byPatternParser =
                ParserFactory.createByPatternParser(
                        rawParsersByPattern,
                        parser
                );

        return new ParserByPatternTextContentProvider(
                contentGetter,
                byPatternParser
        );
    }
}
