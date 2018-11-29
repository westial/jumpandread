package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.westial.alexa.jumpandread.domain.OutputFormatter;
import com.westial.alexa.jumpandread.domain.State;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class Stop implements RequestHandler {
    public static final String INTENT_NAME = "Stop";
    private final State state;
    private final OutputFormatter outputFormatter;

    public Stop(
            State state,
            OutputFormatter outputFormatter
    )
    {
        this.state = state;
        this.outputFormatter = outputFormatter;
    }

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("AMAZON.StopIntent").or(intentName("AMAZON.CancelIntent")).or(intentName("AMAZON.NoIntent")));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        state.updateIntent(INTENT_NAME);
        String speechText = "<emphasis level=\"strong\">Adiós.<break strength=\"x-strong\"/>Busca y lee</emphasis>";
        speechText = outputFormatter.envelop(speechText);
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .build();
    }
}