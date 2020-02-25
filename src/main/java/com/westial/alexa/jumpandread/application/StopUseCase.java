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

        presenter.addText(Presenter.WHISPER_START_TOKEN);
        presenter.addText("salutation.bye.2");
        presenter.addText("presentation.title");
        presenter.addText(Presenter.WHISPER_END_TOKEN);

        return new PresenterView(presenter);
    }
}
