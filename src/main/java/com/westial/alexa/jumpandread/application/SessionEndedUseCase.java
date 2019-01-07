package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.domain.State;
import com.westial.alexa.jumpandread.infrastructure.structure.PresenterView;

public class SessionEndedUseCase
{
    private final State state;
    private final Presenter presenter;

    public enum Reason
    {
        ERROR,
        USER_DID
    }

    public SessionEndedUseCase(
            State state,
            Presenter presenter
    )
    {
        this.state = state;
        this.presenter = presenter;
    }

    public View invoke(String intentName, Reason reason)
    {
        state.updateIntent(intentName);

        if (reason.equals(Reason.ERROR))
        {
            presenter.addText("notice.session.exit.due.error");
        }
        else
        {
            presenter.addText("salutation.see.you.1");
        }

        return new PresenterView(presenter);
    }
}
