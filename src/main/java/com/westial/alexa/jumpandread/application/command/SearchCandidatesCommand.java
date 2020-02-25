package com.westial.alexa.jumpandread.application.command;

import com.westial.alexa.jumpandread.application.exception.NoSearchResultException;
import com.westial.alexa.jumpandread.application.exception.ParsingNoSearchResultException;
import com.westial.alexa.jumpandread.application.exception.UnappropriateContentException;
import com.westial.alexa.jumpandread.application.service.wordcheck.WordsValidator;
import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.infrastructure.exception.SearchException;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.List;

public class SearchCandidatesCommand
{
    private final CandidatesSearch candidatesSearch;

    // Optional, does not validates if null
    private final WordsValidator prohibitedValidator;

    public SearchCandidatesCommand(
            CandidatesSearch candidatesSearch,
            WordsValidator prohibitedValidator
    )
    {
        this.candidatesSearch = candidatesSearch;
        this.prohibitedValidator = prohibitedValidator;
    }

    public Pair<Integer, String> execute(State state, String terms) throws SearchException, NoSearchResultException, UnappropriateContentException
    {
        StringBuilder candidatesList = new StringBuilder();
        if (null != prohibitedValidator && !prohibitedValidator.validate(terms))
        {
            throw new UnappropriateContentException("The search terms contain too much profane words");
        }
        state.createSearchId(terms);
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
        return new ImmutablePair<>(candidates.size(), candidatesList.toString());
    }
}
