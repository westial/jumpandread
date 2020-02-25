package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.domain.State;
import com.westial.alexa.jumpandread.infrastructure.structure.PresenterView;

public class HelpUseCase
{
    private final State state;
    private final Presenter presenter;

    public HelpUseCase(
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

        presenter.addText("command.search.no.terms");
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

        return new PresenterView(presenter);
    }
}
