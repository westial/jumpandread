package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbCandidate;

public class DynamoDbCandidateFactory implements CandidateFactory
{
    private final CandidateGetter getter;
    private final CandidateParser parser;
    private final CandidateRepository repository;
    private static final int FIRST_PARAGRAPH_POSITION = 0;

    public DynamoDbCandidateFactory(
            CandidateGetter getter,
            CandidateParser parser,
            CandidateRepository repository
    )
    {
        this.getter = getter;
        this.parser = parser;
        this.repository = repository;
    }

    @Override
    public Candidate create(
            Integer index,
            User user,
            String searchId,
            String title,
            String url,
            String description
    )
    {
        return new DynamoDbCandidate(
                Candidate.buildId(searchId, index),
                index,
                user.getId(),
                user.getSessionId(),
                searchId,
                title,
                url,
                description,
                getter,
                parser,
                repository,
                FIRST_PARAGRAPH_POSITION
        );
    }

    @Override
    public Candidate create(Integer index, User user, String searchId)
    {
        return new DynamoDbCandidate(
                Candidate.buildId(searchId, index),
                index,
                user.getId(),
                user.getSessionId(),
                searchId,
                getter,
                parser,
                repository
        );
    }
}
