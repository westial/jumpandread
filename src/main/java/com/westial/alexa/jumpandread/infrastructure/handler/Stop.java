package com.westial.alexa.jumpandread.infrastructure.handler;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.westial.alexa.jumpandread.domain.State;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class Stop implements RequestHandler {
    public static final String INTENT_NAME = "Stop";
    private final State state;

    public Stop(
            State state
    )
    {
        this.state = state;
    }

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("AMAZON.StopIntent").or(intentName("AMAZON.CancelIntent")).or(intentName("AMAZON.NoIntent")));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        state.updateIntent(INTENT_NAME);
        String speechText = "<emphasis level=\"strong\">Adiós.<break strength=\"x-strong\"/>Busca y lee</emphasis>";
        return input.getResponseBuilder()
                .withSpeech(speechText)
                .build();
    }
}