package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.application.command.GettingListCommand;
import com.westial.alexa.jumpandread.application.exception.IncompleteStateMandatorySearchException;
import com.westial.alexa.jumpandread.application.exception.NoSearchResultsException;
import com.westial.alexa.jumpandread.domain.MandatorySearchException;
import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.domain.State;
import com.westial.alexa.jumpandread.infrastructure.structure.PresenterView;

public class GettingListUseCase
{
    private final State state;
    private final GettingListCommand listCommand;
    private final Presenter presenter;

    public GettingListUseCase(
            State state,
            GettingListCommand listCommand,
            Presenter presenter
            )
    {
        this.state = state;
        this.listCommand = listCommand;
        this.presenter = presenter;
    }

    public View invoke()
    {
        String searchId = state.getSearchId();
        try
        {
            if (null == searchId)
            {
                throw new IncompleteStateMandatorySearchException("No search yet");
            }
            presenter.addText(listCommand.execute(searchId));

        } catch (NoSearchResultsException | MandatorySearchException exc)
        {
            presenter.addText("warning.no.search.results.empty.list");
        }

        return new PresenterView(presenter);
    }
}
