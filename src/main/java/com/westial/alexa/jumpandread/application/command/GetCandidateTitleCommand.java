package com.westial.alexa.jumpandread.application.command;

import com.westial.alexa.jumpandread.domain.*;

public class GetCandidateTitleCommand
{
    private final CandidateFactory candidateFactory;

    public GetCandidateTitleCommand(
            CandidateFactory candidateFactory
    )
    {
        this.candidateFactory = candidateFactory;
    }

    public String execute(
            State state,
            int candidateIndex
    )
    {
        System.out.printf(
                "DEBUG: Executing Candidate Title Retrieval. Context: candidateIndex %d, user ID as %s, session ID as %s, search ID as %s\n",
                candidateIndex,
                state.getUserId(),
                state.getSessionId(),
                state.getSearchId()
        );
        Candidate candidate = candidateFactory.create(
                candidateIndex,
                new User(state.getUserId(), state.getSessionId()),
                state.getSearchId()
        );

        if (null == candidate)
        {
            throw new RetrievingNoCandidateException(
                    String.format("No Candidate for index as %s", candidateIndex)
            );
        }

        return candidate.getTitle();
    }
}
