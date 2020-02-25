package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.application.command.move.BackwardCommandFactory;
import com.westial.alexa.jumpandread.application.command.move.MoveCommand;
import com.westial.alexa.jumpandread.domain.NoCandidateException;
import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.domain.State;

public class BackwardUseCase extends SafeUseCaseTemplate
{
    private final State state;
    private final BackwardCommandFactory backwardCommandFactory;
    private final String dialogWantContinue;

    public BackwardUseCase(
            State state,
            BackwardCommandFactory backwardCommandFactory,
            Presenter presenter,
            int defaultUnsignedCandidatesFactor,
            int defaultUnsignedParagraphsFactor,
            String dialogWantContinue)
    {
        super(
                presenter,
                defaultUnsignedCandidatesFactor,
                defaultUnsignedParagraphsFactor
        );
        this.state = state;
        this.backwardCommandFactory = backwardCommandFactory;
        this.dialogWantContinue = dialogWantContinue;
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

        try
        {
            presenter.addTexts(
                    backwardCommand.execute(
                            state,
                            candidateIndex,
                            candidatesFactor,
                            paragraphsGroup,
                            paragraphsFactor
                    )
            );

            presenter.addText(Presenter.STRONG_TOKEN);
            presenter.addText(dialogWantContinue);
            presenter.addRepeat(dialogWantContinue);

        } catch (NoCandidateException exc)
        {
            presenter.addText("warning.something.unexpected.on.backwards");
            presenter.addText("command.reading.list");

            System.out.printf(
                    "WARNING: %s. Candidate index: %d, Search Id: %s",
                    exc.getMessage(),
                    candidateIndex,
                    state.getSearchId()
            );
        }

        return presenter;
    }
}
