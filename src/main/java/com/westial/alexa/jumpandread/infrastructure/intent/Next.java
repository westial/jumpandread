package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.westial.alexa.jumpandread.application.ForwardUseCase;
import com.westial.alexa.jumpandread.application.View;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class Next implements RequestHandler
{
    public static final String INTENT_NAME = "Continue";
    private final ForwardUseCase nextUseCase;
    private final int defaultParagraphsGroup;

    public Next(
            ForwardUseCase nextUseCase,
            int defaultParagraphsGroup
            )
    {
        this.nextUseCase = nextUseCase;
        this.defaultParagraphsGroup = defaultParagraphsGroup;
    }

    public boolean canHandle(HandlerInput input)
    {
        return input.matches(
                intentName(INTENT_NAME)
                        .or(intentName("AMAZON.ResumeIntent")
                                .or(intentName("AMAZON.NextIntent")
                                )
                        )
        );
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

        View view = nextUseCase.invoke(
                INTENT_NAME,
                null,
                1,
                0,
                defaultParagraphsGroup
        );

        return input.getResponseBuilder()
                .withSpeech(view.getSpeech())
                .withReprompt(view.getSpeech())
                .build();
    }
}
