package com.westial.alexa.jumpandread.application.command;

import com.westial.alexa.jumpandread.application.exception.AlreadyStepped;
import com.westial.alexa.jumpandread.application.exception.ReadableEndWithXtraContent;
import com.westial.alexa.jumpandread.application.exception.UnappropriateContentException;
import com.westial.alexa.jumpandread.application.service.wordcheck.WordsValidator;
import com.westial.alexa.jumpandread.domain.*;
import org.apache.commons.lang3.ArrayUtils;

import java.util.ArrayList;
import java.util.List;

public abstract class ReadingCommandTemplate
{
    private final CandidateFactory candidateFactory;
    private final ChildrenToSearchCommand childrenCommand;

    // Optional, does not validates if null
    private final WordsValidator wordsValidator;

    public ReadingCommandTemplate(
            CandidateFactory candidateFactory,
            ChildrenToSearchCommand childrenCommand,
            WordsValidator wordsValidator
    )
    {
        this.candidateFactory = candidateFactory;
        this.childrenCommand = childrenCommand;
        this.wordsValidator = wordsValidator;
    }

    public List<Object[]> execute(
            State state,
            int candidateIndex,
            int signedCandidateMovingFactor,
            int paragraphsGroup,
            int paragraphsGroupFactor
    ) throws NoCandidateException
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
        Object[] formats = adaptResult(format, args);
        results.add(formats);
        return results;
    }

    private static Object[] adaptResult(
            String format,
            Object... args
    )
    {
        Object[] formats = new Object[]{format};
        if (null != args)
        {
            formats = ArrayUtils.addAll(formats, args);
        }
        return formats;
    }

    private static List<Object[]> prependResult(
            List<Object[]> results,
            String format,
            Object... args
    )
    {

        Object[] formats = adaptResult(format, args);
        results.add(0, formats);
        return results;
    }

    private List<Object[]> execute(
            State state,
            int candidateIndex,
            int signedCandidateMovingFactor,
            int paragraphsGroup,
            int paragraphsGroupFactor,
            List<Object[]> results
    ) throws NoCandidateException
    {
        Candidate candidate;
        System.out.printf(
                "DEBUG: Executing Paragraphs Retrieval. Context: candidateIndex %d, user ID as %s, session ID as %s, search ID as %s\n",
                candidateIndex,
                state.getUserId(),
                state.getSessionId(),
                state.getSearchId()
        );
        state.updateCandidateIndex(candidateIndex);

        candidate = candidateFactory.create(
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
                        "response.search.result.listing.(index(%s),title(%s),domain(%s))",
                        String.valueOf(candidateIndex),
                        candidate.getTitle(),
                        candidate.getPublishedBy()
                );
                appendResult(results, Presenter.STRONG_TOKEN);
            }
            String content = candidate.dump(paragraphsGroup, Presenter.STRONG_TOKEN);

            if (null != wordsValidator && !wordsValidator.validate(content))
            {
                throw new UnappropriateContentException("Words validator does not validate");
            }

            appendResult(results, Presenter.ALTERNATIVE_VOICE_START_TOKEN);

            appendResult(results, content);

            appendResult(results, Presenter.ALTERNATIVE_VOICE_END_TOKEN);

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

            appendResult(
                    results,
                    "{{ . }}"
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

        } catch (UnappropriateContentException unappropriateExc)
        {
            System.out.printf(
                    "WARNING: %s. Context: candidateIndex %d, user ID as %s, session ID as %s, search ID as %s\n",
                    unappropriateExc.getMessage(),
                    candidateIndex,
                    state.getUserId(),
                    state.getSessionId(),
                    state.getSearchId()
            );

            appendResult(
                    results,
                    "warning.candidate.has.bad.words.(index(%s))",
                    candidateIndex
            );

            appendResult(
                    results,
                    "{{ . }}"
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
        }
    }

    protected abstract void moveParagraphsPoint(
            Candidate candidate,
            int unsignedParagraphsMoveFactor,
            int paragraphsGroup
    );
}
