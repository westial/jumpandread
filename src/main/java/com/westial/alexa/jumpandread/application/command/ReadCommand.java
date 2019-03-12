package com.westial.alexa.jumpandread.application.command;

import com.westial.alexa.jumpandread.domain.*;

public class ReadCommand extends ReadingCommandTemplate
{
    public ReadCommand(
            CandidateFactory candidateFactory,
            ChildrenToSearchCommand childrenCommand
    )
    {
        super(candidateFactory, childrenCommand);
    }


    @Override
    protected void moveParagraphsPoint(
            Candidate candidate,
            int unsignedParagraphsMoveFactor,
            int paragraphsGroup
    )
    {
        candidate.reset();
    }
}
