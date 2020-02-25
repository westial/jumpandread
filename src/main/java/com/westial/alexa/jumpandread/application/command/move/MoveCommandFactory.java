package com.westial.alexa.jumpandread.application.command.move;

import com.westial.alexa.jumpandread.domain.CandidateFactory;

public abstract class MoveCommandFactory
{
    protected final CandidateFactory candidateFactory;

    public MoveCommandFactory(
            CandidateFactory candidateFactory
    )
    {
        this.candidateFactory = candidateFactory;
    }

    public abstract MoveCommand create();
}
