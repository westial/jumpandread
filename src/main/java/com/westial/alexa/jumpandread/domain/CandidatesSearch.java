package com.westial.alexa.jumpandread.domain;

import com.westial.alexa.jumpandread.application.exception.NoSearchResultException;
import com.westial.alexa.jumpandread.infrastructure.exception.SearchException;

import java.util.List;

public interface CandidatesSearch
{
    List<Candidate> find(User user, String searchId, String terms) throws SearchException, NoSearchResultException;
}
