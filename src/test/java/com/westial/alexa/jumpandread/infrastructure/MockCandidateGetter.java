package com.westial.alexa.jumpandread.infrastructure;

import com.westial.alexa.jumpandread.application.exception.GettingCandidateContentException;
import com.westial.alexa.jumpandread.domain.Candidate;
import com.westial.alexa.jumpandread.domain.CandidateGetter;

public class MockCandidateGetter implements CandidateGetter
{
    private final String content;

    public MockCandidateGetter(String forcedContent)
    {
        content = forcedContent;
    }

    public String getContent(Candidate candidate) throws GettingCandidateContentException
    {
        return content;
    }
}
