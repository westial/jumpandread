package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.application.command.GettingListCommand;
import com.westial.alexa.jumpandread.application.exception.IncompleteStateMandatorySearchException;
import com.westial.alexa.jumpandread.application.exception.NoSearchResultsException;
import com.westial.alexa.jumpandread.domain.MandatorySearchException;
import com.westial.alexa.jumpandread.domain.State;

public class GettingListUseCase
{
    private final State state;
    private final GettingListCommand listCommand;

    public GettingListUseCase(State state, GettingListCommand listCommand)
    {
        this.state = state;
        this.listCommand = listCommand;
    }

    public String invoke() throws MandatorySearchException, NoSearchResultsException
    {
        String searchId = state.getSearchId();
        if (null == searchId)
        {
            throw new IncompleteStateMandatorySearchException("No search yet");
        }
        return listCommand.execute(searchId);
    }
}
