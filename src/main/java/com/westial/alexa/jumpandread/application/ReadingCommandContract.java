package com.westial.alexa.jumpandread.application;

import com.westial.alexa.jumpandread.domain.Candidate;

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

    protected String dump(int paragraphsGroup, Candidate candidate, String introduction)
    {
        if (candidate.isFinished())
        {
            throw new NoReadingElements("Finished Candidate");
        }
        return candidate.dump(paragraphsGroup, introduction);
    }
}
