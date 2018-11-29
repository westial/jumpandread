package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.domain.*;

public class NextCommand extends NextReadingCommandContract
{
    private final int paragraphsGroup;
    private final CandidateFactory candidateFactory;

    public NextCommand(
            CandidateFactory candidateFactory,
            int paragraphsGroup
    )
    {
        this.candidateFactory = candidateFactory;
        this.paragraphsGroup = paragraphsGroup;
    }

    @Override
    public String execute(State state) throws MandatorySearchException
    {
        String result;
        System.out.printf(
                "DEBUG: Executing Next. Context: user ID as %s, session ID as %s, search ID as %s\n",
                state.getUserId(),
                state.getSessionId(),
                state.getSearchId()
        );
        Integer candidateIndex = state.getCandidateIndex();

        if (null == candidateIndex)
        {
            throw new IncompleteStateMandatorySearchException("candidateIndex is null yet");
        }

        try
        {
            result = retrieve(candidateIndex, state);
        }
        catch (NoReadingElements noElements)
        {
            System.out.printf(
                    "DEBUG: %s. Context: candidateIndex %d, user ID as %s, session ID as %s, search ID as %s\n",
                    noElements.getMessage(),
                    candidateIndex,
                    state.getUserId(),
                    state.getSessionId(),
                    state.getSearchId()
            );

            try
            {
                System.out.printf(
                        "DEBUG: Jumping to next Candidate. Context: candidateIndex %d, user ID as %s, session ID as %s, search ID as %s\n",
                        candidateIndex,
                        state.getUserId(),
                        state.getSessionId(),
                        state.getSearchId()
                );
                candidateIndex ++;
                result = retrieve(candidateIndex, state);
                state.updateCandidateIndex(candidateIndex);

            }
            catch (NoReadingElements againNoElementsExc)
            {
                throw new CannotContinueMandatoryReadException(
                        String.format(
                                "Again, no reading elements in next retrieved " +
                                        "Candidate after no reading elements " +
                                        "in candidate before. Candidate " +
                                        "index %d. %s",
                                candidateIndex,
                                againNoElementsExc.getMessage()
                        )
                );
            }
        }

        return result;
    }

    private String retrieve(
            Integer candidateIndex,
            State state
            ) throws NoCandidateMandatorySearchException, NoReadingElements
    {
        Candidate candidate = candidateFactory.create(
                candidateIndex,
                new User(state.getUserId(), state.getSessionId()),
                state.getSearchId()
        );

        parse(candidate);
        return dump(
                paragraphsGroup,
                candidate,
                null
        );
    }
}
