package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.application.command.move.BackwardCommandFactory;
import com.westial.alexa.jumpandread.application.command.move.MoveCommand;
import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.domain.State;

public class BackwardUseCase extends SafeUseCaseTemplate
{
    private final State state;
    private final BackwardCommandFactory backwardCommandFactory;

    public BackwardUseCase(
            State state,
            BackwardCommandFactory backwardCommandFactory,
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
        this.backwardCommandFactory = backwardCommandFactory;
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
        MoveCommand backwardCommand = backwardCommandFactory.create();

        state.updateIntent(intentName);

        candidateIndex = state.getCandidateIndex();

        presenter.addTexts(
                backwardCommand.execute(
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
