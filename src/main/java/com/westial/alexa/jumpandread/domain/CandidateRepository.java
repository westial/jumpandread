package com.westial.alexa.jumpandread.domain;

import java.util.List;
import java.util.Set;

public interface CandidateRepository
{
    void update(Candidate candidate);

    Candidate get(String searchId, Integer index);

    int countBySearch(String searchId);

    Set<String> getUniqueUrls(String searchId);

    Integer lastIndexBySearch(String searchId);

    List<Candidate> all(String searchId);
}
