package com.westial.alexa.jumpandread.application.command.move;

import com.westial.alexa.jumpandread.domain.CandidateFactory;

public class BackwardCommandFactory extends MoveCommandFactory
{
    public BackwardCommandFactory(
            CandidateFactory candidateFactory
    )
    {
        super(candidateFactory);
    }

    @Override
    public MoveCommand create()
    {
        return new MoveCommand(
                candidateFactory,
                -1
        );
    }
}
