package com.westial.alexa.jumpandread.domain;

public class User
{
    private String id;
    private final String sessionId;

    public User(String id, String sessionId)
    {
        this.id = id;
        this.sessionId = sessionId;
    }

    public String getId()
    {
        return id;
    }

    public String getSessionId()
    {
        return sessionId;
    }
}
