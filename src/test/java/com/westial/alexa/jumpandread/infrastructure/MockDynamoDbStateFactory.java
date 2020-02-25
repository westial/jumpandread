package com.westial.alexa.jumpandread.infrastructure;

import com.westial.alexa.jumpandread.domain.State;
import com.westial.alexa.jumpandread.domain.StateFactory;
import com.westial.alexa.jumpandread.domain.StateRepository;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbState;

public class MockDynamoDbStateFactory implements StateFactory {
    private final StateRepository repository;
    private State currentState;

    public MockDynamoDbStateFactory(
            StateRepository repository,
            State forcedCurrentState
    ) {
        this.repository = repository;
        currentState = forcedCurrentState;
    }

    @Override
    public State create(
            String userId,
            String sessionId,
            String intent,
            String searchId,
            String searchTerms
    ) {
        currentState = new DynamoDbState(repository, userId, sessionId);
        return currentState;
    }

    @Override
    public State create(String userId, String sessionId) {
        return currentState;
    }
}
