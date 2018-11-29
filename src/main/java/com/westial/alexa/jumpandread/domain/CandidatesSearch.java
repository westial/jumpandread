package com.westial.alexa.jumpandread.domain;

import java.util.List;

public interface CandidatesSearch
{
    List<Candidate> find(User user, String searchId, String terms);
}
