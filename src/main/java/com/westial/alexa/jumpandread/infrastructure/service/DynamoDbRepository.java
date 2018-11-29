package com.westial.alexa.jumpandread.infrastructure.service;

import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapperConfig;

public abstract class DynamoDbRepository
{
    protected static DynamoDBMapper buildMapper(String tableName)
    {
        AmazonDynamoDB dynamoDBClient = AmazonDynamoDBClientBuilder.defaultClient();

        DynamoDBMapperConfig mapperConfig = new DynamoDBMapperConfig.Builder()
                .withTableNameOverride(
                        DynamoDBMapperConfig
                                .TableNameOverride
                                .withTableNameReplacement(tableName))
                .withSaveBehavior(DynamoDBMapperConfig.SaveBehavior.APPEND_SET)
                .build();

        return new DynamoDBMapper(dynamoDBClient,mapperConfig);
    }
}
