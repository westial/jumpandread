package com.westial.alexa.jumpandread.infrastructure.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.westial.alexa.jumpandread.domain.State;
import com.westial.alexa.jumpandread.domain.StateRepository;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbState;


public class DynamoDbStateRepository
        extends DynamoDbRepository
        implements StateRepository
{
    public DynamoDbStateRepository(String tableName)
    {
        super(tableName);
    }

    public DynamoDbStateRepository(String tableName, AmazonDynamoDB dynamoDBClient)
    {
        super(tableName, dynamoDBClient);
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
