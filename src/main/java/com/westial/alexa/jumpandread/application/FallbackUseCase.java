package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.domain.State;
import com.westial.alexa.jumpandread.infrastructure.structure.PresenterView;

public class FallbackUseCase
{
    private final State state;
    private final Presenter presenter;

    public FallbackUseCase(
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

        presenter.addText("warning.no.search.in.this.session.yet");
        presenter.addText("command.search.with.terms");

        return new PresenterView(presenter);
    }
}
