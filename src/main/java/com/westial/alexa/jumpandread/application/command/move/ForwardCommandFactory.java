package com.westial.alexa.jumpandread.application.command.move;

import com.westial.alexa.jumpandread.application.command.ChildrenToSearchCommand;
import com.westial.alexa.jumpandread.application.service.wordcheck.WordsValidator;
import com.westial.alexa.jumpandread.domain.CandidateFactory;

public class ForwardCommandFactory extends MoveCommandFactory
{
    private final ChildrenToSearchCommand childrenCommand;
    private final WordsValidator wordsValidator;

    public ForwardCommandFactory(
            CandidateFactory candidateFactory,
            ChildrenToSearchCommand childrenCommand,
            WordsValidator wordsValidator
    )
    {
        super(candidateFactory);
        this.childrenCommand = childrenCommand;
        this.wordsValidator = wordsValidator;
    }

    @Override
    public MoveCommand create()
    {
        return new MoveCommand(
                candidateFactory,
                1,
                childrenCommand,
                wordsValidator
        );
    }
}
