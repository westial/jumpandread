package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.domain.StateFactory;
import com.westial.alexa.jumpandread.domain.StateRepository;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbState;

public class DynamoDbStateFactory implements StateFactory {
    private final StateRepository repository;

    public DynamoDbStateFactory(StateRepository repository) {
        this.repository = repository;
    }

    @Override
    public DynamoDbState create(
            String userId,
            String sessionId,
            String intent,
            String searchId,
            String searchTerms
    ) {
        return new DynamoDbState(
                repository,
                userId,
                sessionId,
                intent,
                searchId,
                searchTerms
        );
    }

    @Override
    public DynamoDbState create(
            String userId,
            String sessionId
    ) {
        return new DynamoDbState(repository, userId, sessionId);
    }
}
