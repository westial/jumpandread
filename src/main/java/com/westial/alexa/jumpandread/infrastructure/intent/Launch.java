package com.westial.alexa.jumpandread.infrastructure.intent;

import com.amazon.ask.dispatcher.request.handler.HandlerInput;
import com.amazon.ask.dispatcher.request.handler.RequestHandler;
import com.amazon.ask.model.LaunchRequest;
import com.amazon.ask.model.Response;
import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.domain.State;

import java.util.Optional;

import static com.amazon.ask.request.Predicates.intentName;
import static com.amazon.ask.request.Predicates.requestType;

public class Launch implements RequestHandler
{
    private State state;
    private final Presenter presenter;
    public static final String INTENT_NAME = "Launch";

    public Launch(
            State state,
            Presenter presenter
    )
    {
        this.state = state;
        this.presenter = presenter;
    }

    public boolean canHandle(HandlerInput input)
    {
        return input.matches(
                intentName("AMAZON.HelpIntent")
                        .or(requestType(LaunchRequest.class))
        );
    }

    public Optional<Response> handle(HandlerInput input)
    {
        state.updateIntent(INTENT_NAME);
        presenter.addText("presentation.title");
        presenter.addText("{{ . }}{{ . }}");
        presenter.addText("presentation.abstract");
        presenter.addText("{{ . }}{{ . }}");
        presenter.addText("command.search.no.terms");
        presenter.addText("{{ . }}{{ . }}");
        presenter.addText("presentation.getting.started.introduction");
        presenter.addText("{{ . }}{{ . }}");
        presenter.addText("presentation.getting.started.obvious");
        presenter.addText("{{ . }}");
        presenter.addText("presentation.getting.started.jocking");
        presenter.addText("{{ . }}{{ . }}");
        presenter.addText("command.search.with.terms");
        presenter.addText("{{ . }}{{ . }}");
        presenter.addText("command.read.with.number");
        presenter.addText("{{ . }}{{ . }}");
        presenter.addText("command.reading.next");
        presenter.addText("{{ . }}{{ . }}");
        presenter.addText("command.reading.jump");
        presenter.addText("{{ . }}{{ . }}");
        presenter.addText("command.reading.repeat");
        presenter.addText("{{ . }}{{ . }}");
        presenter.addText("command.reading.back");
        presenter.addText("{{ . }}{{ . }}");
        presenter.addText("presentation.finish");
        return input.getResponseBuilder()
                .withSpeech(presenter.output())
                .withReprompt(presenter.output())
                .build();
    }
}
