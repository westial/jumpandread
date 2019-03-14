package com.westial.alexa.jumpandread.infrastructure;

import com.westial.alexa.jumpandread.domain.Candidate;
import com.westial.alexa.jumpandread.domain.CandidateRepository;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

import java.util.*;

public class MockCandidateRepository implements CandidateRepository
{

    private final LinkedHashMap<Pair<Integer, String>, Candidate> candidates;
    private final HashMap<String, List<Candidate>> candidatesBySearchId = new HashMap<>();

    public MockCandidateRepository()
    {
        candidates = new LinkedHashMap<>();
    }

    private void addCandidateBySearchId(String searchId, Candidate candidate)
    {
        if (!candidatesBySearchId.containsKey(searchId))
        {
            candidatesBySearchId.put(searchId, new ArrayList<>());
        }
        candidatesBySearchId.get(searchId).add(candidate);
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
        addCandidateBySearchId(
                forcedCandidate.getSearchId(),
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
            addCandidateBySearchId(
                    forcedCandidate.getSearchId(),
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
        addCandidateBySearchId(
                candidate.getSearchId(),
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
        if (!candidatesBySearchId.containsKey(searchId))
        {
            return 0;
        }
        return candidatesBySearchId.get(searchId).size();
    }

    @Override
    public Set<String> getUniqueUrls(String searchId)
    {
        Set<String> urls = new HashSet<>();
        for (Map.Entry<Pair<Integer, String>, Candidate> candidateEntry: candidates.entrySet())
        {
            Candidate candidate = candidateEntry.getValue();
            urls.add(candidate.getUrl());
        }
        return urls;
    }

    @Override
    public Integer lastIndexBySearch(String searchId)
    {
        Integer last = null;
        for (Map.Entry<Pair<Integer, String>, Candidate> entry: candidates.entrySet())
        {
            Integer index = entry.getKey().getLeft();
            if (null == last || 0 > last.compareTo(index))
            {
                last = index;
            }
        }
        return last;
    }

    @Override
    public List<Candidate> all(String searchId)
    {
        return candidatesBySearchId.get(searchId);
    }

    public String testOnlyGetLastSearchId()
    {
        Candidate last = null;
        for (Candidate candidate : candidates.values())
        {
            last = candidate;
        }
        if (null == last)
        {
            return null;
        }
        return last.getSearchId();
    }
}
