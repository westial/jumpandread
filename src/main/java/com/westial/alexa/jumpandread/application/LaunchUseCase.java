package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.infrastructure.structure.PresenterView;

public class LaunchUseCase
{
    private final State state;
    private final Presenter presenter;

    public LaunchUseCase(
            State state,
            Presenter presenter
    )
    {
        this.state = state;
        this.presenter = presenter;
    }

    public View invoke(String intentName)
    {
        state.updateIntent(intentName);

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

        return new PresenterView(presenter);
    }
}
