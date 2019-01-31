package com.westial.alexa.jumpandread.application.command;

import com.westial.alexa.jumpandread.application.exception.CannotContinueMandatoryReadException;
import com.westial.alexa.jumpandread.application.exception.IteratingNoParagraphsException;
import com.westial.alexa.jumpandread.domain.*;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class ReadingCommandTemplate
{
    private final CandidateFactory candidateFactory;

    public ReadingCommandTemplate(
            CandidateFactory candidateFactory
    )
    {
        this.candidateFactory = candidateFactory;
    }

    public List<Object[]> execute(
            State state,
            int candidateIndex,
            int signedCandidateMovingFactor,
            int paragraphsGroup,
            int paragraphsGroupFactor
    ) throws IteratingNoParagraphsException
    {
        List<Object[]> results = new ArrayList<>();
        return execute(
                state,
                candidateIndex,
                signedCandidateMovingFactor,
                paragraphsGroup,
                paragraphsGroupFactor,
                results
        );
    }

    private static List<Object[]> appendResult(
            List<Object[]> results,
            String format,
            Object... args
    )
    {
        Object[] formats = new Object[]{format};
        if (null != args)
        {
            formats = ArrayUtils.addAll(formats, args);
        }
        results.add(formats);
        return results;
    }

    private List<Object[]> execute(
            State state,
            int candidateIndex,
            int signedCandidateMovingFactor,
            int paragraphsGroup,
            int paragraphsGroupFactor,
            List<Object[]> results
    ) throws IteratingNoParagraphsException
    {
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

        try
        {
            moveParagraphsPoint(candidate, paragraphsGroupFactor, paragraphsGroup);
            if (0 == candidate.getParagraphPosition())
            {
                appendResult(
                        results,
                        "response.search.result.listing.(index(%s),title(%s))",
                        String.valueOf(candidateIndex),
                        candidate.getTitle()
                );
                appendResult(results, Presenter.STRONG_TOKEN);
            }
            String content = dump(paragraphsGroup, candidate);
            appendResult(results, content);
            state.updateCandidateIndex(candidateIndex);
            return results;

        } catch (NoParagraphsException noParagraphsException)
        {
            System.out.printf(
                    "DEBUG: %s. Context: candidateIndex %d, user ID as %s, session ID as %s, search ID as %s\n",
                    noParagraphsException.getMessage(),
                    candidateIndex,
                    state.getUserId(),
                    state.getSessionId(),
                    state.getSearchId()
            );

            appendResult(
                    results,
                    "notice.candidate.has.no.elements.(index(%s))",
                    candidateIndex
            );

            candidateIndex += signedCandidateMovingFactor;

            return execute(
                    state,
                    candidateIndex,
                    signedCandidateMovingFactor,
                    paragraphsGroup,
                    0,
                    results
            );

        } catch (NoCandidateException noCandidateException)
        {
            throw new CannotContinueMandatoryReadException(
                    String.format(
                            "Cannot continue reading due to a no Candidate " +
                                    "exception for index %d. %s",
                            candidateIndex,
                            noCandidateException.getMessage()
                    )
            );
        }
    }

    protected abstract void moveParagraphsPoint(
            Candidate candidate,
            int unsignedParagraphsMoveFactor,
            int paragraphsGroup
    );

    String dump(
            int paragraphsGroup,
            Candidate candidate
    )
    {
        return candidate.dump(
                paragraphsGroup,
                Presenter.STRONG_TOKEN
        );
    }
}
