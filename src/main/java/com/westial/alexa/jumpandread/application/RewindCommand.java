package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.domain.*;

public class RewindCommand extends NextReadingCommandContract
{
    private final int paragraphsGroup;
    private final int backSteps;
    private final CandidateFactory candidateFactory;

    public RewindCommand(
            CandidateFactory candidateFactory,
            int paragraphsGroup,
            int backSteps
    )
    {
        this.candidateFactory = candidateFactory;
        this.paragraphsGroup = paragraphsGroup;
        this.backSteps = backSteps;
    }

    @Override
    public String execute(State state) throws MandatorySearchException
    {
        String result;
        System.out.printf(
                "DEBUG: Executing Continue. Context: user ID as %s, session ID as %s, search ID as %s\n",
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
            return rewindAndRetrieve(candidateIndex, state);
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
                candidateIndex--;
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

    private String rewindAndRetrieve(
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
        candidate.rewind(paragraphsGroup * backSteps);
        return dump(paragraphsGroup, candidate, null);
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
                String.format(
                        "El resultado de búsqueda actual no dispone de más " +
                                "párrafos, salto a leer el siguiente " +
                                "resultado. Resultado de búsqueda número %d, " +
                                "titulado, %s",
                        candidate.getIndex(),
                        candidate.getTitle()
                )
        );
    }
}
