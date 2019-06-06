package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.westial.alexa.jumpandread.application.SearchUseCase;
import com.westial.alexa.jumpandread.application.View;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class Search implements RequestHandler
{
    public static final String INTENT_NAME = "SearchCandidates";
    private static final String TERMS_SLOT_NAME = "searchTerms";
    private final SearchUseCase searchUseCase;


    public Search(SearchUseCase searchUseCase)
    {
        this.searchUseCase = searchUseCase;
    }

    public boolean canHandle(HandlerInput input)
    {
        return input.matches((intentName("AMAZON.SearchQuery")
                .or(intentName(INTENT_NAME)))
        );
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
        StringBuilder searchTerms = new StringBuilder();

        Slot termsSlot;

        if (null != current.getSlots() && null != current.getSlots().get(TERMS_SLOT_NAME))
        {
            termsSlot = current.getSlots().get(TERMS_SLOT_NAME);
            searchTerms.append(termsSlot.getValue());
        }

        View view = searchUseCase.invoke(INTENT_NAME, searchTerms);

        if (0 == searchTerms.length() || view.isEmpty())
        {
            System.out.println("DEBUG: No search terms, delegating directive");

            return input.getResponseBuilder()
                    .addElicitSlotDirective(TERMS_SLOT_NAME, current)
                    .withSpeech(view.getSpeech())
                    .withReprompt(view.getSpeech())
                    .build();
        }

        return input.getResponseBuilder()
                .withSpeech(view.getSpeech())
                .withReprompt(view.getSpeech())
                .build();
    }
}
