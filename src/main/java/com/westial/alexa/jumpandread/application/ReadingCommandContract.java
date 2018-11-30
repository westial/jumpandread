package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.domain.Candidate;
import com.westial.alexa.jumpandread.domain.Presenter;

public abstract class ReadingCommandContract
{
    protected void parse(Candidate candidate) throws NoReadingElements
    {
        if (null == candidate.getParagraphs())
        {
            candidate.provideContent();
        }
        candidate.parse();
    }

    String dump(
            int paragraphsGroup,
            Candidate candidate,
            String introduction
    )
    {
        if (candidate.isFinished())
        {
            throw new NoReadingElements("Finished Candidate");
        }
        return candidate.dump(
                paragraphsGroup,
                introduction,
                Presenter.STRONG_TOKEN
        );
    }
}
