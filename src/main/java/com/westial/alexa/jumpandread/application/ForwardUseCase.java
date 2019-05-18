package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.application.command.CountCandidatesBySearchCommand;
import com.westial.alexa.jumpandread.application.command.move.ForwardCommandFactory;
import com.westial.alexa.jumpandread.application.command.move.MoveCommand;
import com.westial.alexa.jumpandread.domain.NoCandidateException;
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

        try
        {
            presenter.addTexts(
                    forwardCommand.execute(
                            state,
                            state.getCandidateIndex(),
                            candidatesFactor,
                            paragraphsGroup,
                            paragraphsFactor
                    )
            );
        } catch (NoCandidateException exc)
        {
            if (countCandidatesCommand.execute(state.getSearchId()) < state.getCandidateIndex())
            {
                presenter.addText(
                        "warning.forward.into.candidate.out.of.bounds(index(%s))",
                        String.valueOf(state.getCandidateIndex())
                );
            }
            else
            {
                presenter.addText("warning.something.unexpected.on.forward");
            }

            presenter.addText("command.reading.list");

            System.out.printf(
                    "WARNING: %s. Candidate index: %d, Search Id: %s",
                    exc.getMessage(),
                    state.getCandidateIndex(),
                    state.getSearchId()
            );
        }

        return presenter;
    }
}
