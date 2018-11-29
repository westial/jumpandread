package com.westial.alexa.jumpandread.infrastructure.structure;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.westial.alexa.jumpandread.domain.State;
import com.westial.alexa.jumpandread.domain.StateRepository;

import java.util.Calendar;

public class DynamoDbState extends State
{
    public DynamoDbState()
    {
        super(null, null, null, null, null);
    }

    public DynamoDbState(StateRepository repository, String userId, String sessionId)
    {
        super(repository, userId, sessionId);
    }

    public DynamoDbState(StateRepository repository, String userId, String sessionId, String intent, String searchId)
    {
        super(repository, userId, sessionId, intent, searchId);
    }

    @Override
    @DynamoDBAttribute(attributeName = "intent")
    public String getIntent()
    {
        return super.getIntent();
    }

    @Override
    @DynamoDBAttribute(attributeName = "user_id")
    public String getUserId()
    {
        return super.getUserId();
    }

    @Override
    @DynamoDBAttribute(attributeName = "session_id")
    public String getSessionId()
    {
        return super.getSessionId();
    }

    @Override
    @DynamoDBAttribute(attributeName = "timestamp")
    public Calendar getTimestamp()
    {
        return super.getTimestamp();
    }

    @Override
    @DynamoDBAttribute(attributeName = "search_id")
    public String getSearchId()
    {
        return super.getSearchId();
    }

    @Override
    @DynamoDBAttribute(attributeName = "candidate_index")
    public Integer getCandidateIndex()
    {
        return super.getCandidateIndex();
    }

    @Override
    @DynamoDBHashKey(attributeName = "id")
    public String getId()
    {
        return super.id;
    }


    //
    // Required by DynamoDB SDK
    //

    public void setId(String id)
    {
        super.id = id;
    }

    public void setUserId(String userId)
    {
        super.userId = userId;
    }

    public void setSessionId(String sessionId)
    {
        super.sessionId = sessionId;
    }

    public void setSearchId(String searchId)
    {
        super.searchId = searchId;
    }

    public void setIntent(String intent)
    {
        super.intent = intent;
    }

    public void setTimestamp(Calendar timestamp)
    {
        super.timestamp = timestamp;
    }

    public void setCandidateIndex(Integer candidateIndex)
    {
        super.candidateIndex = candidateIndex;
    }
}
