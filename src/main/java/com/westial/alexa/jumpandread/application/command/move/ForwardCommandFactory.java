package com.westial.alexa.jumpandread.application.command.move;

import com.westial.alexa.jumpandread.domain.CandidateFactory;

public class ForwardCommandFactory extends MoveCommandFactory
{
    public ForwardCommandFactory(
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
                1
        );
    }
}
