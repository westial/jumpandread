package com.westial.alexa.jumpandread.infrastructure.service;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.westial.alexa.jumpandread.domain.State;
import com.westial.alexa.jumpandread.domain.StateRepository;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbState;


public class DynamoDbStateRepository
        extends DynamoDbRepository
        implements StateRepository
{
    private static DynamoDBMapper dbMapper;

    public DynamoDbStateRepository(String tableName)
    {
        dbMapper = buildMapper(tableName);
    }

    public void update(State state)
    {
        dbMapper.save(state);
    }

    public State get(String userId, String sessionId)
    {
        return dbMapper.load(
                DynamoDbState.class,
                DynamoDbState.buildId(userId, sessionId)
        );
    }
}
