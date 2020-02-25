package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.westial.alexa.jumpandread.application.LaunchUseCase;
import com.westial.alexa.jumpandread.application.View;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.requestType;

public class Launch implements RequestHandler
{
    public static final String INTENT_NAME = "Launch";
    private final LaunchUseCase launchUseCase;

    public Launch(
            LaunchUseCase launchUseCase
    )
    {
        this.launchUseCase = launchUseCase;
    }

    public boolean canHandle(HandlerInput input)
    {
        return input.matches(requestType(LaunchRequest.class));
    }

    public Optional<Response> handle(HandlerInput input)
    {
        View view = launchUseCase.invoke(INTENT_NAME);

        return input.getResponseBuilder()
                .withSpeech(view.getSpeech())
                .withReprompt(view.getReprompt())
                .build();
    }
}
