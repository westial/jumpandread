package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.domain.*;

public class ReadCommand extends ReadingCommandContract
{
    private final int paragraphsGroup;
    private final CandidateFactory candidateFactory;

    public ReadCommand(
            CandidateFactory candidateFactory,
            int paragraphsGroup
    )
    {
        this.candidateFactory = candidateFactory;
        this.paragraphsGroup = paragraphsGroup;
    }

    public String execute(
            State state,
            int candidateIndex
    ) throws NoReadingElements
    {
        String introduction;
        String result;
        System.out.printf(
                "DEBUG: Executing Paragraphs Retrieval. Context: candidateIndex %d, user ID as %s, session ID as %s, search ID as %s\n",
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

        introduction = String.format(
                "Resultado de búsqueda número %d, titulado, %s",
                candidate.getIndex(),
                candidate.getTitle()
        );

        candidate.reset();

        try
        {
            result = retrieve(candidate, introduction);
            state.updateCandidateIndex(candidateIndex);

        } catch (NoReadingElements noElements)
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
                candidate = candidateFactory.create(
                        candidateIndex,
                        new User(state.getUserId(), state.getSessionId()),
                        state.getSearchId()
                );
                introduction = String.format(
                        "El resultado de búsqueda actual no dispone de más " +
                                "párrafos, salto a leer el siguiente " +
                                "resultado. Resultado de búsqueda número %d, " +
                                "titulado, %s",
                        candidate.getIndex(),
                        candidate.getTitle()
                );
                result = retrieve(candidate, introduction);
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
        state.updateCandidateIndex(candidateIndex);
        return result;
    }

    private String retrieve(Candidate candidate, String introduction) throws NoCandidateMandatorySearchException, NoReadingElements
    {
        parse(candidate);
        return dump(
                paragraphsGroup,
                candidate,
                introduction
        );
    }
}
