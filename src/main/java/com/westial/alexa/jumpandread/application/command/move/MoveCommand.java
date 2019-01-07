package com.westial.alexa.jumpandread.application.command.move;

import com.westial.alexa.jumpandread.application.command.ReadingCommandTemplate;
import com.westial.alexa.jumpandread.domain.*;

import static java.lang.Math.abs;

public class MoveCommand extends ReadingCommandTemplate
{
    private final int sign;

    public MoveCommand(
            CandidateFactory candidateFactory,
            int sign
    )
    {
        super(candidateFactory);
        this.sign = sign;
    }

    @Override
    protected void moveParagraphsPoint(
            Candidate candidate,
            int unsignedParagraphsMoveFactor,
            int paragraphsGroup
    )
    {
        if (unsignedParagraphsMoveFactor != 0)
        {
            if (0 < sign)
            {
                candidate.forward(
                        paragraphsGroup * abs(unsignedParagraphsMoveFactor)
                );
            }
            else
            {
                candidate.rewind(
                        paragraphsGroup * abs(unsignedParagraphsMoveFactor)
                );
            }
        }
    }
}
