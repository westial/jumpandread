package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.domain.State;
import com.westial.alexa.jumpandread.domain.StateFactory;

public class ProvideStateCommand
{

    private final StateFactory factory;

    public ProvideStateCommand(StateFactory factory)
    {
        this.factory = factory;
    }

    public State execute(String userId, String sessionId)
    {
        return factory.create(userId, sessionId);
    }
}
