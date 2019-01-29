package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.Candidate;
import com.westial.alexa.jumpandread.domain.CandidateFactory;
import com.westial.alexa.jumpandread.domain.CandidateRepository;
import com.westial.alexa.jumpandread.domain.User;
import com.westial.alexa.jumpandread.domain.content.TextContentProvider;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbCandidate;

public class DynamoDbCandidateFactory implements CandidateFactory
{
    private final CandidateRepository repository;
    private final int maxParagraphsNumber;
    private static final int FIRST_PARAGRAPH_POSITION = 0;
    private final TextContentProvider contentProvider;

    public DynamoDbCandidateFactory(
            TextContentProvider contentProvider,
            CandidateRepository repository,
            int maxParagraphsNumber
    )
    {
        this.contentProvider = contentProvider;
        this.repository = repository;
        this.maxParagraphsNumber = maxParagraphsNumber;
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
                contentProvider,
                repository,
                FIRST_PARAGRAPH_POSITION,
                maxParagraphsNumber
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
                contentProvider,
                repository,
                maxParagraphsNumber
        );
    }
}
