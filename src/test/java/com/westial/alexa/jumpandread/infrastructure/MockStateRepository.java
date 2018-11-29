package com.westial.alexa.jumpandread.infrastructure;

import com.westial.alexa.jumpandread.domain.StateRepository;
import com.westial.alexa.jumpandread.domain.State;

public class MockStateRepository implements StateRepository
{
    private State current;

    public void update(State state)
    {
        current = state;
    }

    public State get(String userId, String sessionId)
    {
        return current;
    }
}
