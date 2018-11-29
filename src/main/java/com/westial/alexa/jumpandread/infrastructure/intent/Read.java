package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.amazon.ask.model.Slot;
import com.westial.alexa.jumpandread.application.ReadCommand;
import com.westial.alexa.jumpandread.domain.OutputFormatter;
import com.westial.alexa.jumpandread.domain.State;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class Read extends SafeIntent
{
    public static final String INTENT_NAME = "ReadCandidate";
    private static final String CANDIDATE_INDEX_SLOT_NAME = "candidateIndex";
    private final State state;
    private final ReadCommand retrieveCommand;

    public Read(
            State state,
            ReadCommand retrieveCommand,
            OutputFormatter outputFormatter
    )
    {
        super(outputFormatter);

        this.state = state;

        this.retrieveCommand = retrieveCommand;
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

        speech = outputFormatter.envelop(
                retrieveCommand.execute(
                        state,
                        candidateIndex
                )
        );

        return input.getResponseBuilder()
                .withSpeech(speech)
                .withReprompt(speech)
                .build();
    }
}
