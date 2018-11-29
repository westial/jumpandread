package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.westial.alexa.jumpandread.application.JumpCommand;
import com.westial.alexa.jumpandread.domain.OutputFormatter;
import com.westial.alexa.jumpandread.domain.State;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class Jump extends SafeIntent
{
    public static final String INTENT_NAME = "Jump";
    private final State state;
    private final JumpCommand jumpCommand;

    public Jump(
            State state,
            JumpCommand jumpCommand,
            OutputFormatter outputFormatter
    )
    {
        super(outputFormatter);

        this.state = state;

        this.jumpCommand = jumpCommand;
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

        speech = outputFormatter.envelop(
                jumpCommand.execute(
                        state
                )
        );

        return input.getResponseBuilder()
                .withSpeech(speech)
                .withReprompt(speech)
                .build();
    }
}
