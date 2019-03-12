package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.application.command.CountCandidatesBySearchCommand;
import com.westial.alexa.jumpandread.application.command.move.ForwardCommandFactory;
import com.westial.alexa.jumpandread.application.command.move.MoveCommand;
import com.westial.alexa.jumpandread.application.exception.ReadableEndWithXtraContent;
import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.domain.State;

public class ForwardUseCase extends SafeUseCaseTemplate
{
    private final State state;
    private final ForwardCommandFactory forwardCommandFactory;
    private final CountCandidatesBySearchCommand countCandidatesCommand;

    public ForwardUseCase(
            State state,
            ForwardCommandFactory forwardCommandFactory,
            CountCandidatesBySearchCommand countCandidatesCommand,
            Presenter presenter,
            int defaultUnsignedCandidatesFactor,
            int defaultUnsignedParagraphsFactor
    )
    {
        super(
                presenter,
                defaultUnsignedCandidatesFactor,
                defaultUnsignedParagraphsFactor
        );
        this.state = state;
        this.forwardCommandFactory = forwardCommandFactory;
        this.countCandidatesCommand = countCandidatesCommand;
    }

    @Override
    protected Presenter safeInvoke(
            String intentName,
            Integer candidateIndex,
            int candidatesFactor,
            int paragraphsFactor,
            int paragraphsGroup
    )
    {
        MoveCommand forwardCommand = forwardCommandFactory.create();

        state.updateIntent(intentName);

        candidateIndex = state.getCandidateIndex();

        presenter.addTexts(
                forwardCommand.execute(
                        state,
                        candidateIndex,
                        candidatesFactor,
                        paragraphsGroup,
                        paragraphsFactor
                )
        );

        return presenter;
    }
}
