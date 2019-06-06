package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.westial.alexa.jumpandread.application.FallbackUseCase;
import com.westial.alexa.jumpandread.application.View;

import java.util.Optional;

/**
 * This intent has to be configured alone because always can handle itself.
 * It's an intent to force user search something. Used, for example, when a new
 * user tries request an intent that requires some steps before.
 */
public class Fallback implements RequestHandler
{
    public static final String INTENT_NAME = "Fallback";
    private final FallbackUseCase fallbackUseCase;


    public Fallback(FallbackUseCase fallbackUseCase)
    {
        this.fallbackUseCase = fallbackUseCase;
    }

    @Override
    public boolean canHandle(HandlerInput input)
    {
        return true;
    }

    @Override
    public Optional<Response> handle(HandlerInput input)
    {
        View view = fallbackUseCase.invoke(INTENT_NAME);

        return input.getResponseBuilder()
                .withSpeech(view.getSpeech())
                .withReprompt(view.getSpeech())
                .build();
    }
}
