package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.westial.alexa.jumpandread.application.HelpUseCase;
import com.westial.alexa.jumpandread.application.View;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class Help implements RequestHandler
{
    public static final String INTENT_NAME = "Help";
    private final HelpUseCase helpUseCase;

    public Help(
            HelpUseCase helpUseCase
    )
    {
        this.helpUseCase = helpUseCase;
    }

    public boolean canHandle(HandlerInput input)
    {
        return input.matches(intentName("AMAZON.HelpIntent"));
    }

    public Optional<Response> handle(HandlerInput input)
    {
        View view = helpUseCase.invoke(INTENT_NAME);

        return input.getResponseBuilder()
                .withSpeech(view.getSpeech())
                .withReprompt(view.getSpeech())
                .build();
    }
}
