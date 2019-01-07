package com.westial.alexa.jumpandread.infrastructure;

import com.westial.alexa.jumpandread.application.exception.GettingCandidateContentException;
import com.westial.alexa.jumpandread.domain.Candidate;
import com.westial.alexa.jumpandread.domain.CandidateGetter;

import java.util.Queue;

public class MockQueueCandidateGetter implements CandidateGetter
{
    private Queue<String> contents;

    public MockQueueCandidateGetter(Queue<String> contents)
    {
        this.contents = contents;
    }

    @Override
    public String getContent(Candidate candidate) throws GettingCandidateContentException
    {
        return contents.remove();
    }
}
