package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.application.exception.CannotContinueMandatoryReadException;
import com.westial.alexa.jumpandread.domain.Presenter;
import com.westial.alexa.jumpandread.infrastructure.structure.PresenterView;

public abstract class SafeUseCaseTemplate
{
    protected Presenter presenter;
    protected final int defaultUnsignedCandidatesFactor;
    protected final int defaultUnsignedParagraphsFactor;

    public SafeUseCaseTemplate(
            Presenter presenter,
            int defaultUnsignedCandidatesFactor,
            int defaultUnsignedParagraphsFactor
            )
    {
        this.presenter = presenter;
        this.defaultUnsignedCandidatesFactor = defaultUnsignedCandidatesFactor;
        this.defaultUnsignedParagraphsFactor = defaultUnsignedParagraphsFactor;
    }

    public View invoke(String intentName, Integer candidateIndex, int paragraphsGroup)
    {
        return invoke(
                intentName,
                candidateIndex,
                defaultUnsignedCandidatesFactor,
                defaultUnsignedParagraphsFactor,
                paragraphsGroup
        );
    }

    public View invoke(
            String intentName,
            Integer candidateIndex,
            int unsignedCandidatesFactor,
            int unsignedParagraphsFactor,
            int paragraphsGroup
            )
    {
        try
        {
            presenter = safeInvoke(
                    intentName,
                    candidateIndex,
                    unsignedCandidatesFactor,
                    unsignedParagraphsFactor,
                    paragraphsGroup
                    );

        } catch (CannotContinueMandatoryReadException excReading)
        {
            excReading.printStackTrace();
            presenter.addText("notice.no.more.paragraphs");

        }

        return new PresenterView(presenter);
    }

    protected abstract Presenter safeInvoke(
            String intentName,
            Integer candidateIndex,
            int unsignedCandidatesFactor,
            int unsignedParagraphsFactor,
            int paragraphsGroup
    );
}
