package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.application.command.CountCandidatesBySearchCommand;
import com.westial.alexa.jumpandread.application.command.GetCandidateTitleCommand;
import com.westial.alexa.jumpandread.application.command.ReadCommand;
import com.westial.alexa.jumpandread.domain.Candidate;
import com.westial.alexa.jumpandread.domain.NoCandidateException;
import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.domain.State;

import java.util.List;

public class CurrentUseCase extends SafeUseCaseTemplate
{
    private final State state;
    private final GetCandidateTitleCommand getTitleCommand;
    private final CountCandidatesBySearchCommand countCandidatesCommand;
    private final ReadCommand readCommand;
    private final Presenter presenter;

    public CurrentUseCase(
            State state,
            GetCandidateTitleCommand getTitleCommand,
            CountCandidatesBySearchCommand countCandidatesCommand,
            ReadCommand readCommand,
            Presenter presenter,
            int defaultUnsignedCandidatesFactor,
            int defaultUnsignedParagraphsFactor
    )
    {
        super(presenter, defaultUnsignedCandidatesFactor, defaultUnsignedParagraphsFactor);
        this.state = state;
        this.getTitleCommand = getTitleCommand;
        this.countCandidatesCommand = countCandidatesCommand;
        this.readCommand = readCommand;
        this.presenter = presenter;
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
        state.updateIntent(intentName);

        try
        {
            List<Object[]> readingResult = readCommand.execute(
                    state,
                    candidateIndex,
                    candidatesFactor,
                    paragraphsGroup,
                    paragraphsFactor
            );

            presenter.addTexts(readingResult);

        } catch (NoCandidateException noCandidate)
        {
            presenter.clear();
            Integer candidatesCount = countCandidatesCommand.execute(
                    state.getSearchId()
            );
            if (0 == candidatesCount)
            {
                presenter.addText("warning.no.search.in.this.session.yet");
                presenter.addText("command.search.with.terms");
                System.out.printf(
                        "WARNING: %s. Search Id: %s",
                        noCandidate.getMessage(),
                        state.getSearchId()
                );
            }
            else
            {
                presenter.addText(
                        "warning.no.candidate.for.this.index.(index(%s),start(%s),end(%s))",
                        String.valueOf(candidateIndex),
                        String.valueOf(Candidate.INDEX_START),
                        String.valueOf(candidatesCount -1 + Candidate.INDEX_START)
                );
                System.out.printf(
                        "WARNING: %s. Candidate index: %d, start %d, end %d",
                        noCandidate.getMessage(),
                        candidateIndex,
                        Candidate.INDEX_START,
                        candidatesCount -1 + Candidate.INDEX_START
                );
            }
        }

        return presenter;
    }
}
