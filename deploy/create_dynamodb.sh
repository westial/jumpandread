#!/usr/bin/env bash

export AWS_PROFILE=westial_usa

aws dynamodb create-table --table-name "jnr_candidate_master" \
    --attribute-definitions \
        "AttributeName=id,AttributeType=S" \
        "AttributeName=search_id,AttributeType=S" \
        "AttributeName=index,AttributeType=N" \
    --key-schema "AttributeName=id,KeyType=HASH" \
    --provisioned-throughput \
        "ReadCapacityUnits=1,WriteCapacityUnits=1" \
    --global-secondary-indexes \
        "IndexName=search_id-index-index,KeySchema=[{AttributeName=search_id,KeyType=HASH},{AttributeName=index,KeyType=RANGE}],Projection={ProjectionType=ALL},ProvisionedThroughput={ReadCapacityUnits=2,WriteCapacityUnits=1}"

aws dynamodb create-table --table-name "jnr_state_master" \
    --attribute-definitions \
        "AttributeName=id,AttributeType=S" \
    --key-schema "AttributeName=id,KeyType=HASH" \
    --provisioned-throughput \
        "ReadCapacityUnits=1,WriteCapacityUnits=1"