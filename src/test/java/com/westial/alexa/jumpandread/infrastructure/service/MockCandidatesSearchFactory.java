package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.CandidateFactory;
import com.westial.alexa.jumpandread.domain.CandidatesSearch;
import com.westial.alexa.jumpandread.domain.Configuration;

public class MockCandidatesSearchFactory implements CandidatesSearchFactory
{
    private final CandidatesSearch candidatesSearch;

    public MockCandidatesSearchFactory(CandidatesSearch candidatesSearch)
    {
        this.candidatesSearch = candidatesSearch;
    }

    @Override
    public CandidatesSearch create(Configuration config, CandidateFactory candidateFactory)
    {
        return candidatesSearch;
    }
}
