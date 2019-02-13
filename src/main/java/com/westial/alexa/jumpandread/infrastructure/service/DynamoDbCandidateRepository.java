package com.westial.alexa.jumpandread.infrastructure.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.westial.alexa.jumpandread.domain.Candidate;
import com.westial.alexa.jumpandread.domain.CandidateRepository;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbCandidate;

import java.util.HashMap;
import java.util.Map;

public class DynamoDbCandidateRepository
        extends DynamoDbRepository
        implements CandidateRepository
{
    public DynamoDbCandidateRepository(String tableName)
    {
        super(tableName);
    }

    public DynamoDbCandidateRepository(String tableName, AmazonDynamoDB dynamoDBClient)
    {
        super(tableName, dynamoDBClient);
    }

    @Override
    public void update(Candidate candidate)
    {
        dbMapper.save(candidate);
    }

    @Override
    public Candidate get(String searchId, Integer index)
    {
        return dbMapper.load(
                DynamoDbCandidate.class,
                DynamoDbCandidate.buildId(searchId, index)
        );
    }

    @Override
    public int countBySearch(String searchId)
    {
        Map<String, AttributeValue> searchValues = new HashMap<>();
        searchValues.put(":searchId", new AttributeValue().withS(searchId));
        DynamoDBQueryExpression<DynamoDbCandidate> queryExpression =
                new DynamoDBQueryExpression<DynamoDbCandidate>()
                        .withIndexName("search_id-index")
                        .withConsistentRead(false)
                        .withKeyConditionExpression("search_id = :searchId")
                        .withExpressionAttributeValues(searchValues);
        return dbMapper.count(DynamoDbCandidate.class, queryExpression);
    }
}
