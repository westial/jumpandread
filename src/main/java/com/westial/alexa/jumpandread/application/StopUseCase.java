package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.domain.State;
import com.westial.alexa.jumpandread.infrastructure.structure.PresenterView;

public class StopUseCase
{
    private final State state;
    private final Presenter presenter;

    public StopUseCase(
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

        presenter.addText("{{ whisper }}");
        presenter.addText("salutation.bye.2");
        presenter.addText("{{ . }}");
        presenter.addText("presentation.title");
        presenter.addText("{{ end whisper }}");

        return new PresenterView(presenter);
    }
}
