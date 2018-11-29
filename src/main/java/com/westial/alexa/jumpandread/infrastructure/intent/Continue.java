package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.model.Intent;
import com.amazon.ask.model.IntentRequest;
import com.amazon.ask.model.Response;
import com.westial.alexa.jumpandread.application.NextReadingCommandContract;
import com.westial.alexa.jumpandread.domain.OutputFormatter;
import com.westial.alexa.jumpandread.domain.State;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

// TODO can this class and Jump class be simplified? They look quite similar.
public class Continue extends SafeIntent
{
    public static final String INTENT_NAME = "Continue";
    private final State state;
    private final NextReadingCommandContract continueCommand;

    public Continue(
            State state,
            NextReadingCommandContract continueCommand,
            OutputFormatter outputFormatter
    )
    {
        super(outputFormatter);

        this.state = state;
        this.continueCommand = continueCommand;
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
                continueCommand.execute(
                        state
                )
        );

        return input.getResponseBuilder()
                .withSpeech(speech)
                .withReprompt(speech)
                .build();
    }
}
