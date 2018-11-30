package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.domain.*;

import java.util.List;

public class SearchCandidatesCommand
{
    private final CandidatesSearch candidatesSearch;

    public SearchCandidatesCommand(
            CandidatesSearch candidatesSearch)
    {
        this.candidatesSearch = candidatesSearch;
    }

    public String execute(State state, String terms)
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
            return null;
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
