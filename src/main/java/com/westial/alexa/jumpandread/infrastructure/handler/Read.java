package com.westial.alexa.jumpandread.infrastructure.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.westial.alexa.jumpandread.application.RetrieveParagraphsCommand;
import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.infrastructure.service.*;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class Read extends SafeHandler
{
    public static final String INTENT_NAME = "ReadCandidate";
    private static final String CANDIDATE_INDEX_SLOT_NAME = "candidateIndex";
    private final State state;
    private final RetrieveParagraphsCommand retrieve;

    public Read(
            State state,
            Configuration config
    )
    {
        super(new AlexaOutputFormatter());

        this.state = state;

        CandidateParser candidateParser = new JsoupCandidateParser();
        CandidateGetter candidateGetter = new UnirestCandidateGetter(
                config.retrieve("HTTP_USER_AGENT")
        );

        CandidateRepository candidateRepository = new DynamoDbCandidateRepository(
                config.retrieve("CANDIDATE_TABLE_NAME")
        );
        CandidateFactory candidateFactory = new DynamoDbCandidateFactory(
                candidateGetter,
                candidateParser,
                candidateRepository,
                outputFormatter
        );

        retrieve = new RetrieveParagraphsCommand(
                candidateFactory,
                Integer.parseInt(config.retrieve("PARAGRAPHS_GROUP_MEMBERS_COUNT"))
        );
    }

    public boolean canHandle(HandlerInput input)
    {
        return input.matches(intentName(INTENT_NAME));
    }

    @Override
    public Optional<Response> safeHandle(HandlerInput input)
    {
        state.updateIntent(INTENT_NAME);
        String speech;
        IntentRequest request = (IntentRequest) input.getRequestEnvelope().getRequest();
        Intent current = request.getIntent();
        System.out.format(
                "DEBUG: Handling requested intent name %s with custom name as %s\n",
                current.getName(),
                INTENT_NAME
        );
        Slot candidateIndexSlot = current.getSlots().get(CANDIDATE_INDEX_SLOT_NAME);
        int candidateIndex = Integer.parseInt(candidateIndexSlot.getValue());

        speech = retrieve.execute(
                state,
                candidateIndex
        );

        return input.getResponseBuilder()
                .withSpeech(speech)
                .withReprompt(speech)
                .build();
    }
}
