package com.westial.alexa.jumpandread.infrastructure.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.westial.alexa.jumpandread.application.JumpCommand;
import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.infrastructure.service.*;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class Jump extends SafeHandler
{
    public static final String INTENT_NAME = "Jump";
    private final State state;
    private final JumpCommand retrieveNext;

    public Jump(
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

        retrieveNext = new JumpCommand(
                candidateFactory,
                Integer.parseInt(config.retrieve("PARAGRAPHS_GROUP_MEMBERS_COUNT")),
                outputFormatter
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

        Integer candidateIndex = state.getCandidateIndex();

        System.out.format(
                "DEBUG: Current state candidate index %d\n",
                candidateIndex
        );

        speech = retrieveNext.execute(
                state
        );

        return input.getResponseBuilder()
                .withSpeech(speech)
                .withReprompt(speech)
                .build();
    }
}
