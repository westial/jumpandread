package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.domain.State;

public abstract class NextReadingCommandContract extends ReadingCommandContract
{
    public abstract String execute(State state);
}
