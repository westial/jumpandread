package com.westial.alexa.jumpandread.infrastructure.structure;

import com.westial.alexa.jumpandread.application.View;
import com.westial.alexa.jumpandread.domain.Presenter;

public class PresenterView implements View
{
    private Presenter presenter;

    public PresenterView(Presenter presenter)
    {
        this.presenter = presenter;
    }

    @Override
    public String getSpeech()
    {
        return presenter.output();
    }

    @Override
    public boolean isEmpty()
    {
        return presenter.isEmpty();
    }
}
