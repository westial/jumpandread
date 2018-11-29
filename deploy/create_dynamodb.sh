#!/usr/bin/env bash

export AWS_PROFILE=westial

aws dynamodb create-table --table-name "jnr_candidate" \
    --attribute-definitions \
        "AttributeName=id,AttributeType=S" \
        "AttributeName=search_id,AttributeType=S" \
    --key-schema "AttributeName=id,KeyType=HASH" \
    --provisioned-throughput \
        "ReadCapacityUnits=1,WriteCapacityUnits=1" \
    --global-secondary-indexes \
        "IndexName=search_id-index,KeySchema=[{AttributeName=search_id,KeyType=HASH}],Projection={ProjectionType=ALL},ProvisionedThroughput={ReadCapacityUnits=2,WriteCapacityUnits=1}"

aws dynamodb create-table --table-name "jnr_state" \
    --attribute-definitions \
        "AttributeName=id,AttributeType=S" \
    --key-schema "AttributeName=id,KeyType=HASH" \
    --provisioned-throughput \
        "ReadCapacityUnits=1,WriteCapacityUnits=1"