package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.domain.State;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class Stop implements RequestHandler {
    public static final String INTENT_NAME = "Stop";
    private final State state;
    private final Presenter presenter;

    public Stop(
            State state,
            Presenter presenter
    )
    {
        this.state = state;
        this.presenter = presenter;
    }

    @Override
    public boolean canHandle(HandlerInput input) {
        return input.matches(intentName("AMAZON.StopIntent").or(intentName("AMAZON.CancelIntent")).or(intentName("AMAZON.NoIntent")));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        state.updateIntent(INTENT_NAME);
        presenter.addText("{{ whisper }}");
        presenter.addText("salutation.bye.2");
        presenter.addText("{{ . }}");
        presenter.addText("presentation.title");
        presenter.addText("{{ end whisper }}");
        return input.getResponseBuilder()
                .withSpeech(presenter.output())
                .build();
    }
}