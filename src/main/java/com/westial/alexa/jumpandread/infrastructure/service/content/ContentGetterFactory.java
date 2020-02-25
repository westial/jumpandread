package com.westial.alexa.jumpandread.infrastructure.service.content;

import com.westial.alexa.jumpandread.domain.content.AddressModifier;
import com.westial.alexa.jumpandread.domain.content.ContentGetter;
import com.westial.alexa.jumpandread.infrastructure.exception.InitializationError;
import com.westial.alexa.jumpandread.infrastructure.structure.ParserType;

import java.util.Map;

public class ContentGetterFactory
{

    public AddressModifier createByType(
            String parserType
    )
    {
        switch (ParserType.valueOf(parserType))
        {
            case WebSearch:
            case WebNarrative:
                return new DefaultAddressModifier();

            case CustomMedium:
                return new MediumAddressModifier();

            default:
                throw new InitializationError(
                        String.format(
                                "Missing appropriate content getter for " +
                                        "configuration PARSER_TYPE as %s",
                                parserType
                        )
                );
        }
    }

    public ByPatternContentGetterDecorator buildByPatternContentGetter(
            Map<String, String> parsersByPatternConfig,
            ContentGetter defaultGetter,
            AddressModifier defaultModifier
    )
    {
        ByPatternContentGetterDecorator byPatternGetter =
                new ByPatternContentGetterDecorator(
                        defaultGetter,
                        defaultModifier
                );

        for (Map.Entry<String, String> entry: parsersByPatternConfig.entrySet())
        {
            byPatternGetter.addModifier(
                    entry.getKey(),
                    createByType(entry.getValue())
            );
        }

        return byPatternGetter;
    }
}
