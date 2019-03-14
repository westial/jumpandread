package com.westial.alexa.jumpandread.application.command;

import com.westial.alexa.jumpandread.application.exception.AlreadyStepped;
import com.westial.alexa.jumpandread.application.exception.CannotContinueMandatoryReadException;
import com.westial.alexa.jumpandread.application.exception.ReadableEndWithXtraContent;
import com.westial.alexa.jumpandread.domain.*;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class ReadingCommandTemplate
{
    private final CandidateFactory candidateFactory;
    private final ChildrenToSearchCommand childrenCommand;

    public ReadingCommandTemplate(
            CandidateFactory candidateFactory,
            ChildrenToSearchCommand childrenCommand
    )
    {
        this.candidateFactory = candidateFactory;
        this.childrenCommand = childrenCommand;
    }

    public List<Object[]> execute(
            State state,
            int candidateIndex,
            int signedCandidateMovingFactor,
            int paragraphsGroup,
            int paragraphsGroupFactor
    )
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
    )
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
            String content = candidate.dump(paragraphsGroup, Presenter.STRONG_TOKEN);

            appendResult(results, content);
            state.updateCandidateIndex(candidateIndex);
            return results;

        } catch (ReadableEndWithXtraContent xtraContent)
        {
            try
            {
                String childrenList = childrenCommand.execute(candidate);
                appendResult(
                        results,
                        "warning.no.more.paragraphs.but.children"
                );
                appendResult(results, Presenter.STRONG_TOKEN);
                appendResult(results, childrenList);
            } catch (AlreadyStepped alreadyStepped)
            {
                appendResult(
                        results,
                        "warning.no.more.paragraphs.but.children.already.in.list"
                );
            }

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
                    "warning.candidate.has.no.elements.(index(%s))",
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
}
