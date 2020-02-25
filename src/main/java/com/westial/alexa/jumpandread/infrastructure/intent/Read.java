package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.westial.alexa.jumpandread.application.CurrentUseCase;
import com.westial.alexa.jumpandread.application.View;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class Read implements RequestHandler
{
    public static final String INTENT_NAME = "ReadCandidate";
    private static final String CANDIDATE_INDEX_SLOT_NAME = "candidateIndex";
    private final CurrentUseCase currentUseCase;
    private final int defaultParagraphsGroup;

    public Read(
            CurrentUseCase currentUseCase,
            int defaultParagraphsGroup
    )
    {
        this.currentUseCase = currentUseCase;
        this.defaultParagraphsGroup = defaultParagraphsGroup;
    }

    public boolean canHandle(HandlerInput input)
    {
        return input.matches(intentName(INTENT_NAME));
    }

    public Optional<Response> handle(HandlerInput input)
    {
        IntentRequest request = (IntentRequest) input.getRequestEnvelope().getRequest();
        Intent current = request.getIntent();
        System.out.format(
                "DEBUG: Handling requested intent name %s with custom name as %s\n",
                current.getName(),
                INTENT_NAME
        );
        Slot candidateIndexSlot = current.getSlots().get(CANDIDATE_INDEX_SLOT_NAME);
        int candidateIndex = Integer.parseInt(candidateIndexSlot.getValue());

        View view = currentUseCase.invoke(
                INTENT_NAME,
                candidateIndex,
                defaultParagraphsGroup
        );

        return input.getResponseBuilder()
                .withSpeech(view.getSpeech())
                .withReprompt(view.getReprompt())
                .build();
    }
}
