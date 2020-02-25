package com.westial.alexa.jumpandread.domain;

import com.westial.alexa.jumpandread.domain.content.ContentCounter;

public class CandidateContentCounter extends ContentCounter
{
    /**
     * Initializes a new counter starting at 0, with the given id.
     *
     * @param id the name of the counter
     */
    public CandidateContentCounter(String id)
    {
        super(id);
    }
}
