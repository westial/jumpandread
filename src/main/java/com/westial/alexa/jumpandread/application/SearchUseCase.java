package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.application.command.SearchCandidatesCommand;
import com.westial.alexa.jumpandread.application.exception.NoSearchResultsException;
import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.domain.State;
import com.westial.alexa.jumpandread.infrastructure.exception.SearchException;
import com.westial.alexa.jumpandread.infrastructure.structure.PresenterView;

public class SearchUseCase
{
    private final State state;
    private final SearchCandidatesCommand searchCommand;
    private final Presenter presenter;

    public SearchUseCase(
            State state,
            SearchCandidatesCommand searchCommand,
            Presenter presenter
    )
    {
        this.state = state;
        this.searchCommand = searchCommand;
        this.presenter = presenter;
    }

    public View invoke(String intentName, String searchTerms)
    {
        state.updateIntent(intentName);

        if (null == searchTerms || searchTerms.isEmpty())
        {
            presenter.addText("dialog.search.what");
        }
        else
        {
            try
            {
                presenter.addText(searchCommand.execute(state, searchTerms));

            } catch (SearchException e)
            {
                presenter.addText("warning.search.exception");
                System.out.printf("ERROR: %s\n", e.getMessage());
                e.printStackTrace();

            } catch (NoSearchResultsException e)
            {
                presenter.addText("notice.no.search.results");
                presenter.addText("dialog.search.want.other");
                presenter.addText(Presenter.STRONG_TOKEN);
                presenter.addText("dialog.search.what");
            }
        }

        return new PresenterView(presenter);
    }
}
