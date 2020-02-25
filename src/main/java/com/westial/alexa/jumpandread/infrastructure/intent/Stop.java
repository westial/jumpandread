package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.westial.alexa.jumpandread.application.StopUseCase;
import com.westial.alexa.jumpandread.application.View;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class Stop implements RequestHandler {
    public static final String INTENT_NAME = "Stop";
    private final StopUseCase stopUseCase;

    public Stop(
            StopUseCase stopUseCase
    )
    {
        this.stopUseCase = stopUseCase;
    }

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("AMAZON.StopIntent")
                .or(intentName("AMAZON.CancelIntent"))
        );
    }

    @Override
    public Optional<Response> handle(HandlerInput input)
    {
        View view = stopUseCase.invoke(INTENT_NAME);

        return input.getResponseBuilder()
                .withSpeech(view.getSpeech())
                .build();
    }
}