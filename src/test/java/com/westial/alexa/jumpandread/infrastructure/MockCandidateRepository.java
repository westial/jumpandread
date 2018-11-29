package com.westial.alexa.jumpandread.infrastructure;

import com.westial.alexa.jumpandread.domain.Candidate;
import com.westial.alexa.jumpandread.domain.CandidateRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.HashMap;
import java.util.List;

public class MockCandidateRepository implements CandidateRepository
{

    private final HashMap<Pair<Integer, String>, Candidate> candidates;

    public MockCandidateRepository()
    {
        candidates = new HashMap<>();
    }

    public MockCandidateRepository(Candidate forcedCandidate)
    {
        this();
        candidates.put(
                buildKey(
                        forcedCandidate.getIndex(),
                        forcedCandidate.getSearchId()
                ),
                forcedCandidate
        );
    }

    public MockCandidateRepository(List<Candidate> forcedCandidates)
    {
        this();
        for (Candidate forcedCandidate: forcedCandidates)
        {
            candidates.put(
                    buildKey(
                            forcedCandidate.getIndex(),
                            forcedCandidate.getSearchId()
                    ),
                    forcedCandidate
            );
        }
    }

    private Pair<Integer, String> buildKey(int index, String searchId)
    {
        return new ImmutablePair<>(
                index,
                searchId
        );
    }

    public void update(Candidate candidate)
    {
        candidates.put(
                buildKey(
                        candidate.getIndex(),
                        candidate.getSearchId()
                ),
                candidate
        );
    }

    public Candidate get(String searchId, Integer index)
    {
        return candidates.get(buildKey(index, searchId));
    }

    @Override
    public int countBySearch(String searchId)
    {
        return candidates.size();
    }
}
