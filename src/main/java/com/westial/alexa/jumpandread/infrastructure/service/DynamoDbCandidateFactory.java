package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.domain.content.TextContentProvider;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbCandidate;

public class DynamoDbCandidateFactory implements CandidateFactory
{
    private final CandidateRepository repository;
    private final int maxParagraphsNumber;
    private final PagerEdgesCalculator partCalculator;
    private final TextContentProvider contentProvider;

    public DynamoDbCandidateFactory(
            TextContentProvider contentProvider,
            CandidateRepository repository,
            int maxParagraphsNumber,
            PagerEdgesCalculator partCalculator
    )
    {
        this.contentProvider = contentProvider;
        this.repository = repository;
        this.maxParagraphsNumber = maxParagraphsNumber;
        this.partCalculator = partCalculator;
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
                Candidate.FIRST_PARAGRAPH_POSITION,
                maxParagraphsNumber,
                partCalculator
        );
    }

    @Override
    public Candidate create(Integer index, User user, String searchId) throws NoCandidateException
    {
        return new DynamoDbCandidate(
                Candidate.buildId(searchId, index),
                index,
                user.getId(),
                user.getSessionId(),
                searchId,
                contentProvider,
                repository,
                maxParagraphsNumber,
                partCalculator
        );
    }
}
