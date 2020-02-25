package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.CandidateFactory;
import com.westial.alexa.jumpandread.domain.CandidatesSearch;
import com.westial.alexa.jumpandread.domain.Configuration;

public interface CandidatesSearchFactory
{
    CandidatesSearch create(
            Configuration config,
            CandidateFactory candidateFactory
    );
}
