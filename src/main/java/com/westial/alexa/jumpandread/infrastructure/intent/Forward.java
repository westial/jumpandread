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

public class Forward implements RequestHandler
{
    public static final String INTENT_NAME = "Forward";
    private final ForwardUseCase forwardUseCase;
    private final int defaultParagraphsGroup;
    private final int defaultParagraphsFactor;

    public Forward(
            ForwardUseCase forwardUseCase,
            int defaultParagraphsGroup,
            int defaultParagraphsFactor
    )
    {

        this.forwardUseCase = forwardUseCase;
        this.defaultParagraphsGroup = defaultParagraphsGroup;
        this.defaultParagraphsFactor = defaultParagraphsFactor;
    }

    public boolean canHandle(HandlerInput input)
    {
        return input.matches(
                intentName(INTENT_NAME)
                        .or(intentName("AMAZON.PageDownIntent"))
                        .or(intentName("AMAZON.ScrollDownIntent"))
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

        View view = forwardUseCase.invoke(
                INTENT_NAME,
                null,
                1,
                defaultParagraphsFactor,
                defaultParagraphsGroup
        );

        return input.getResponseBuilder()
                .withSpeech(view.getSpeech())
                .withReprompt(view.getSpeech())
                .build();
    }
}
