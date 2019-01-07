package com.westial.alexa.jumpandread.infrastructure.service;

import com.westial.alexa.jumpandread.application.command.ProvideStateCommand;
import com.westial.alexa.jumpandread.domain.State;
import com.westial.alexa.jumpandread.domain.StateFactory;

public class StateManager
{
    private final StateFactory factory;

    public StateManager(StateFactory factory)
    {
        this.factory = factory;
    }

    public State provide(String userId, String sessionId)
    {
        ProvideStateCommand command = new ProvideStateCommand(factory);
        return command.execute(userId, sessionId);
    }
}
