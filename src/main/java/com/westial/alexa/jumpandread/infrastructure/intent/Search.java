package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.westial.alexa.jumpandread.application.SearchCandidatesCommand;
import com.westial.alexa.jumpandread.domain.OutputFormatter;
import com.westial.alexa.jumpandread.domain.State;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class Search implements RequestHandler
{
    public static final String INTENT_NAME = "SearchCandidates";
    private static final String TERMS_SLOT_NAME = "searchTerms";

    private final SearchCandidatesCommand searchCommand;
    private final State state;
    private final OutputFormatter outputFormatter;

    public Search(
            State state,
            SearchCandidatesCommand searchCommand,
            OutputFormatter outputFormatter
    )
    {
        this.state = state;
        this.searchCommand = searchCommand;
        this.outputFormatter = outputFormatter;
    }

    public boolean canHandle(HandlerInput input)
    {
        return input.matches((intentName("AMAZON.SearchQuery")
                .or(intentName(INTENT_NAME)))
        );
    }

    public Optional<Response> handle(HandlerInput input)
    {
        String speech;
        state.updateIntent(INTENT_NAME);
        IntentRequest request = (IntentRequest) input.getRequestEnvelope().getRequest();
        Intent current = request.getIntent();
        System.out.format(
                "DEBUG: Handling requested intent name %s with custom name as %s\n",
                current.getName(),
                INTENT_NAME
        );
        Slot termsSlot = current.getSlots().get(TERMS_SLOT_NAME);
        String searchTerms = termsSlot.getValue();

        if (null == searchTerms || searchTerms.isEmpty())
        {
            System.out.println("DEBUG: No search terms, delegating directive");

            speech = "¿Qué quieres buscar?";

            speech = outputFormatter.envelop(speech);

            return input.getResponseBuilder()
                    .addElicitSlotDirective(TERMS_SLOT_NAME, current)
                    .withSpeech(speech)
                    .withReprompt(speech)
                    .build();
        }

        speech = searchCommand.execute(state, searchTerms);

        if (null == speech)
        {
            System.out.println("DEBUG: Null results, ask for new terms");

            speech = outputFormatter.envelop(
                    "Lo siento mucho, pero no he encontrado ningun resultado con tus criterios de búsqueda."
            );

            speech += "¿Quieres probar a buscar otra cosa?";

            speech = outputFormatter.envelop(speech);

            return input.getResponseBuilder()
                    .addElicitSlotDirective(TERMS_SLOT_NAME, current)
                    .withSpeech(speech)
                    .withReprompt(speech)
                    .build();
        }

        speech = outputFormatter.envelop(speech);

        return input.getResponseBuilder()
                .withSpeech(speech)
                .withReprompt(speech)
                .build();
    }
}
