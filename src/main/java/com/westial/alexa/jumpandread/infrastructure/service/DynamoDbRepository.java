package com.westial.alexa.jumpandread.infrastructure.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

abstract class DynamoDbRepository
{
    final DynamoDBMapper dbMapper;

    DynamoDbRepository(String tableName)
    {
        this(tableName, AmazonDynamoDBClientBuilder.defaultClient());
    }

    DynamoDbRepository(String tableName, AmazonDynamoDB dynamoDBClient)
    {
        DynamoDBMapperConfig mapperConfig = new DynamoDBMapperConfig.Builder()
                .withTableNameOverride(
                        DynamoDBMapperConfig
                                .TableNameOverride
                                .withTableNameReplacement(tableName))
                .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.APPEND_SET)
                .build();

        dbMapper = new DynamoDBMapper(dynamoDBClient, mapperConfig);
    }
}
