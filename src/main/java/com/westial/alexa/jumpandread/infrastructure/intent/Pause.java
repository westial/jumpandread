package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.westial.alexa.jumpandread.application.PauseUseCase;
import com.westial.alexa.jumpandread.application.View;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class Pause implements RequestHandler {

    public static final String INTENT_NAME = "Pause";
    private final PauseUseCase pauseUseCase;

    public Pause(
            PauseUseCase pauseUseCase)
    {
        this.pauseUseCase = pauseUseCase;
    }

    @Override
    public boolean canHandle(HandlerInput input)
    {
        return input.matches(intentName("AMAZON.PauseIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        View view = pauseUseCase.invoke(INTENT_NAME);

        return input.getResponseBuilder()
                .withSpeech(view.getSpeech())
                .withReprompt(view.getSpeech())
                .build();
    }
}