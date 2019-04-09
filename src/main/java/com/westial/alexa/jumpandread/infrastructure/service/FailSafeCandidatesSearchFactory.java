package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.CandidateFactory;
import com.westial.alexa.jumpandread.domain.CandidatesSearch;
import com.westial.alexa.jumpandread.domain.Configuration;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.stream.Collectors;

public class FailSafeCandidatesSearchFactory implements CandidatesSearchFactory
{
    private final LinkedList<CandidatesSearchFactory> candidatesSearchFactories;

    public FailSafeCandidatesSearchFactory(CandidatesSearchFactory... candidatesSearchFactories)
    {
        this.candidatesSearchFactories = new LinkedList<>(Arrays.asList(candidatesSearchFactories));
    }

    @Override
    public CandidatesSearch create(Configuration config, CandidateFactory candidateFactory)
    {
        LinkedList<CandidatesSearch> engines = candidatesSearchFactories
                .stream()
                .map(factory -> factory.create(config, candidateFactory))
                .collect(Collectors.toCollection(LinkedList::new));

        return new FailSafeCandidatesSearch(engines);
    }
}
