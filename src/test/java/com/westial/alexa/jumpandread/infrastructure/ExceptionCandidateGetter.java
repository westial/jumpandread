package com.westial.alexa.jumpandread.infrastructure;

import com.westial.alexa.jumpandread.application.exception.GettingCandidateContentException;
import com.westial.alexa.jumpandread.domain.Candidate;
import com.westial.alexa.jumpandread.domain.CandidateGetter;

public class ExceptionCandidateGetter implements CandidateGetter
{
    private final String message;

    public ExceptionCandidateGetter(String message)
    {
        this.message = message;
    }

    public String getContent(Candidate candidate)
    {
        throw new GettingCandidateContentException(message);
    }
}
