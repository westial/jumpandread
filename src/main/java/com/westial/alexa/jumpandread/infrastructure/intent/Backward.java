package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.westial.alexa.jumpandread.application.BackwardUseCase;
import com.westial.alexa.jumpandread.application.View;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class Backward implements RequestHandler
{
    public static final String INTENT_NAME = "Backward";
    private final BackwardUseCase rewindUseCase;
    private final int defaultParagraphsGroup;

    public Backward(
            BackwardUseCase rewindUseCase,
            int defaultParagraphsGroup
    )
    {
        this.rewindUseCase = rewindUseCase;
        this.defaultParagraphsGroup = defaultParagraphsGroup;
    }

    public boolean canHandle(HandlerInput input)
    {
        return input.matches(intentName(INTENT_NAME));
    }

    @Override
    public Optional<Response> handle(HandlerInput input)
    {
        IntentRequest request = (IntentRequest) input.getRequestEnvelope().getRequest();
        Intent current = request.getIntent();
        System.out.format(
                "DEBUG: Handling requested intent name %s with custom name as %s\n",
                current.getName(),
                INTENT_NAME
        );

        View view = rewindUseCase.invoke(
                INTENT_NAME,
                null,
                1,
                4,
                defaultParagraphsGroup
        );

        return input.getResponseBuilder()
                .withSpeech(view.getSpeech())
                .withReprompt(view.getSpeech())
                .build();
    }
}
