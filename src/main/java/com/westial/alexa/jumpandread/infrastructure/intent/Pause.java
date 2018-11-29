package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.westial.alexa.jumpandread.domain.OutputFormatter;
import com.westial.alexa.jumpandread.domain.State;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class Pause implements RequestHandler {

    private final State state;
    private final OutputFormatter outputFormatter;
    public static final String INTENT_NAME = "Pause";

    public Pause(
            State state,
            OutputFormatter outputFormatter
    )
    {
        this.state = state;
        this.outputFormatter = outputFormatter;
    }

    @Override
    public boolean canHandle(HandlerInput input)
    {
        return input.matches(intentName("AMAZON.PauseIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        state.updateIntent(INTENT_NAME);
        String speech = "<amazon:effect name=\"whispered\">Pausa." +
                "<break strength=\"strong\"/> Para continuar dime" +
                "<break strength=\"strong\"/>: <break strength=\"x-strong\"/>" +
                "<emphasis level=\"strong\">Alexa, continuar</emphasis>" +
                "</amazon:effect>";
        speech = outputFormatter.envelop(speech);
        return input.getResponseBuilder()
                .withSpeech(speech)
                .withReprompt(speech)
                .build();
    }
}