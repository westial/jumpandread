package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.domain.*;

public class JumpCommand extends ReadingCommandContract
{
    private final int paragraphsGroup;
    private final OutputFormatter outputFormatter;
    private final CandidateFactory candidateFactory;

    public JumpCommand(
            CandidateFactory candidateFactory,
            int paragraphsGroup,
            OutputFormatter outputFormatter
    )
    {
        this.candidateFactory = candidateFactory;
        this.paragraphsGroup = paragraphsGroup;
        this.outputFormatter = outputFormatter;
    }

    public String execute(State state) throws MandatorySearchException
    {
        String result;
        System.out.printf(
                "DEBUG: Executing Paragraphs Jumping. Context: user ID as %s, session ID as %s, search ID as %s\n",
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
            return forwardAndRetrieve(candidateIndex, state);
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

    private String forwardAndRetrieve(
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
        candidate.forward(paragraphsGroup);
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
