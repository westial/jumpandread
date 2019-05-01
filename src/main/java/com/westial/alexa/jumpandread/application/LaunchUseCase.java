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
        presenter.addText(Presenter.LONGEST_TOKEN);
        presenter.addText("presentation.abstract");
        presenter.addText(Presenter.LONGEST_TOKEN);
        presenter.addText("command.search.no.terms");
        presenter.addText(Presenter.LONGEST_TOKEN);
        presenter.addText("presentation.getting.started.introduction");
        presenter.addText(Presenter.LONGEST_TOKEN);
        presenter.addText("command.search.with.terms");
        presenter.addText(Presenter.LONGEST_TOKEN);
        presenter.addText("command.read.with.number");
        presenter.addText(Presenter.LONGEST_TOKEN);
        presenter.addText("command.reading.next");
        presenter.addText(Presenter.LONGEST_TOKEN);
        presenter.addText("command.reading.jump");
        presenter.addText(Presenter.LONGEST_TOKEN);
        presenter.addText("command.reading.repeat");
        presenter.addText(Presenter.LONGEST_TOKEN);
        presenter.addText("command.reading.back");
        presenter.addText(Presenter.LONGEST_TOKEN);
        presenter.addText("command.reading.list");
        presenter.addText(Presenter.LONGEST_TOKEN);
        presenter.addText("presentation.finish");

        return new PresenterView(presenter);
    }
}
