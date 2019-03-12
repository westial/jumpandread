package com.westial.alexa.jumpandread.infrastructure.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBQueryExpression;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.westial.alexa.jumpandread.domain.Candidate;
import com.westial.alexa.jumpandread.domain.CandidateRepository;
import com.westial.alexa.jumpandread.infrastructure.structure.DynamoDbCandidate;

import java.util.*;
import java.util.stream.Collectors;

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
        return dbMapper.count(
                DynamoDbCandidate.class,
                createSearchIdRetrievalExpression(searchId)
        );
    }

    private DynamoDBQueryExpression<DynamoDbCandidate> createSearchIdRetrievalExpression(String searchId)
    {
        Map<String, AttributeValue> searchValues = new HashMap<>();
        searchValues.put(":searchId", new AttributeValue().withS(searchId));
        return new DynamoDBQueryExpression<DynamoDbCandidate>()
                .withIndexName("search_id-index-index")
                .withConsistentRead(false)
                .withKeyConditionExpression("search_id = :searchId")
                .withExpressionAttributeValues(searchValues);
    }

    @Override
    public Set<String> getUniqueUrls(String searchId)
    {
        DynamoDBQueryExpression<DynamoDbCandidate> expression =
                createSearchIdRetrievalExpression(searchId);

        Map<String, String> projectionNames = new HashMap<>();
        projectionNames.put("#url", "url");

        expression = expression
                .withProjectionExpression("#url")
                .withExpressionAttributeNames(projectionNames);

        List<DynamoDbCandidate> items = dbMapper.query(
                DynamoDbCandidate.class,
                expression
        );

        return items
                .stream()
                .map(DynamoDbCandidate::getUrl)
                .collect(Collectors.toSet());
    }

    @Override
    public Integer lastIndexBySearch(String searchId)
    {
        DynamoDBQueryExpression<DynamoDbCandidate> expression =
                createSearchIdRetrievalExpression(searchId);

        Map<String, String> projectionNames = new HashMap<>();
        projectionNames.put("#index", "index");

        expression = expression
                .withScanIndexForward(false)
                .withProjectionExpression("#index")
                .withExpressionAttributeNames(projectionNames)
                .withLimit(1);

        List<DynamoDbCandidate> items = dbMapper.query(
                DynamoDbCandidate.class,
                expression
        );

        return items.get(0).getIndex();
    }
}
