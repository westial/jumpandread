package com.westial.alexa.jumpandread.application.command.move;

import com.westial.alexa.jumpandread.application.command.ChildrenToSearchCommand;
import com.westial.alexa.jumpandread.domain.CandidateFactory;

public class BackwardCommandFactory extends MoveCommandFactory
{
    private final ChildrenToSearchCommand childrenCommand;

    public BackwardCommandFactory(
            CandidateFactory candidateFactory,
            ChildrenToSearchCommand childrenCommand
    )
    {
        super(candidateFactory);
        this.childrenCommand = childrenCommand;
    }

    @Override
    public MoveCommand create()
    {
        return new MoveCommand(
                candidateFactory,
                -1,
                childrenCommand
        );
    }
}
