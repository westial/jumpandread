package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.domain.*;
import com.westial.alexa.jumpandread.infrastructure.structure.PresenterView;

import static java.lang.Math.abs;

public class PauseUseCase
{
    private final State state;
    private final Presenter presenter;
    private CandidateFactory candidateFactory;

    public PauseUseCase(
            State state,
            CandidateFactory candidateFactory,
            Presenter presenter
    )
    {
        this.state = state;
        this.presenter = presenter;
        this.candidateFactory = candidateFactory;
    }

    public View invoke(
            String intentName,
            int unsignedParagraphsMoveFactor,
            int paragraphsGroup
    )
    {
        state.updateIntent(intentName);

        Candidate candidate = candidateFactory.create(
                state.getCandidateIndex(),
                new User(state.getUserId(), state.getSessionId()),
                state.getSearchId()
        );

        candidate.rewind(
                paragraphsGroup * abs(unsignedParagraphsMoveFactor)
        );

        candidate.persist();

        presenter.addText("notice.after.pause");

        return new PresenterView(presenter);
    }
}
