package com.westial.alexa.jumpandread.application.command;

import com.westial.alexa.jumpandread.application.exception.NoSearchResultException;
import com.westial.alexa.jumpandread.application.exception.ParsingNoSearchResultException;
import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.infrastructure.exception.SearchException;

import java.util.List;

public class SearchCandidatesCommand
{
    private final CandidatesSearch candidatesSearch;

    public SearchCandidatesCommand(
            CandidatesSearch candidatesSearch)
    {
        this.candidatesSearch = candidatesSearch;
    }

    public String execute(State state, String terms) throws SearchException, NoSearchResultException
    {
        StringBuilder candidatesList = new StringBuilder();
        state.updateSearchId();
        List<Candidate> candidates = candidatesSearch.find(
                new User(
                        state.getUserId(),
                        state.getSessionId()
                ),
                state.getSearchId(),
                terms
        );
        if (null == candidates)
        {
            throw new ParsingNoSearchResultException(
                    "Apparently there is any result but no Candidate has been built"
            );
        }
        state.updateCandidateIndex(
                candidates.get(0).getIndex()
        );
        for(Candidate candidate: candidates)
        {
            candidate.persist();
            candidatesList.append(candidate.buildListing(Presenter.STRONG_TOKEN));
        }
        return candidatesList.toString();
    }
}
