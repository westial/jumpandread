package com.westial.alexa.jumpandread.domain;

import java.util.Calendar;

import static java.util.UUID.randomUUID;

public abstract class State
{
    private StateRepository repository;

    protected String id;
    protected String userId;

    protected String sessionId;
    protected String intent;
    protected String searchId;
    protected String searchTerms;
    protected Calendar timestamp;
    protected Integer candidateIndex;

    public State(
            StateRepository repository,
            String userId,
            String sessionId,
            String intent,
            String searchId,
            String searchTerms
    )
    {
        this.repository = repository;
        this.userId = userId;
        this.sessionId = sessionId;
        this.intent = intent;
        this.searchId = searchId;
        this.searchTerms = searchTerms;

        this.id = buildId(this.userId, this.sessionId);
    }

    public State(
            StateRepository repository,
            String userId,
            String sessionId)
    {
        this(repository, userId, sessionId, null, null, null);
        System.out.printf(
                "DEBUG: Trying to retrieve State from repository. Context: " +
                        "user ID as %s, session ID as %s\n",
                userId,
                sessionId
        );
        State state = repository.get(userId, sessionId);
        if (null != state)
        {
            System.out.printf(
                    "DEBUG: State retrieved from repository. Context: " +
                            "user ID as %s, session ID as %s, search " +
                            "ID as %s, candidateIndex as %d, " +
                            "Intent as %s, terms as %s\n",
                    state.getUserId(),
                    state.getSessionId(),
                    state.getSearchId(),
                    state.getCandidateIndex(),
                    state.getIntent(),
                    state.getSearchTerms()
            );
            searchId = state.getSearchId();
            candidateIndex = state.getCandidateIndex();
            intent = state.getIntent();
            searchTerms = state.getSearchTerms();
        }
    }

    public static String buildId(String userId, String sessionId)
    {
        return userId;
        // return String.format("%s:%s", userId, sessionId);
    }

    public void createSearchId(String terms)
    {
        searchId = randomUUID().toString();
        searchTerms = terms;
        candidateIndex = null;
        persist();
    }

    public void updateCandidateIndex(Integer index)
    {
        candidateIndex = index;
        persist();
    }

    public void updateIntent(String intent)
    {
        System.out.format("DEBUG: Updating intent to %s\n", intent);
        this.intent = intent;
        persist();
    }

    public void persist()
    {
        this.timestamp = Calendar.getInstance();
        repository.update(this);
    }

    public Calendar getTimestamp()
    {
        return timestamp;
    }

    public String getIntent()
    {
        return intent;
    }

    public String getUserId()
    {
        return userId;
    }

    public String getSessionId()
    {
        return sessionId;
    }

    public String getSearchId()
    {
        return searchId;
    }

    public String getSearchTerms()
    {
        return searchTerms;
    }

    public String getId()
    {
        return id;
    }

    public Integer getCandidateIndex()
    {
        return candidateIndex;
    }
}
