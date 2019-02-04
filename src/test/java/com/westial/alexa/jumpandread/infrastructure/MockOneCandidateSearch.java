package com.westial.alexa.jumpandread.infrastructure;

import com.westial.alexa.jumpandread.application.exception.NoSearchResultsException;
import com.westial.alexa.jumpandread.domain.Candidate;
import com.westial.alexa.jumpandread.domain.CandidateFactory;
import com.westial.alexa.jumpandread.domain.CandidatesSearch;
import com.westial.alexa.jumpandread.domain.User;
import com.westial.alexa.jumpandread.infrastructure.exception.SearchException;

import java.util.ArrayList;
import java.util.List;

public class MockOneCandidateSearch implements CandidatesSearch
{
    private final Candidate forcedCandidate;
    private final CandidateFactory factory;

    public MockOneCandidateSearch(
            Candidate forcedCandidate,
            CandidateFactory factory
    )
    {
        this.forcedCandidate = forcedCandidate;
        this.factory = factory;
    }

    public List<Candidate> find(User user, String searchId, String terms) throws SearchException, NoSearchResultsException
    {
        List<Candidate> candidates = new ArrayList<>();
        Candidate candidate = factory.create(
                forcedCandidate.getIndex(),
                new User(forcedCandidate.getUserId(), forcedCandidate.getSessionId()),
                searchId,
                forcedCandidate.getTitle(),
                forcedCandidate.getUrl(),
                forcedCandidate.getDescription()
        );
        candidates.add(candidate);
        return candidates;
    }
}

