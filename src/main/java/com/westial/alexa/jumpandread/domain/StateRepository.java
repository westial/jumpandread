package com.westial.alexa.jumpandread.domain;

public interface StateRepository
{
    void update(State state);

    State get(String userId, String sessionId);
}
