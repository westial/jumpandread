package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.Response;
import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.domain.State;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;

public class Pause implements RequestHandler {

    private final State state;
    private final Presenter presenter;
    public static final String INTENT_NAME = "Pause";

    public Pause(
            State state,
            Presenter presenter
    )
    {
        this.state = state;
        this.presenter = presenter;
    }

    @Override
    public boolean canHandle(HandlerInput input)
    {
        return input.matches(intentName("AMAZON.PauseIntent"));
    }

    @Override
    public Optional<Response> handle(HandlerInput input) {
        state.updateIntent(INTENT_NAME);
        presenter.addText("notice.after.pause");
        return input.getResponseBuilder()
                .withSpeech(presenter.output())
                .withReprompt(presenter.output())
                .build();
    }
}