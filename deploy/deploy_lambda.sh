#!/usr/bin/env bash

SCRIPT_DIR="$( cd "$( dirname "${BASH_SOURCE[0]}" )" >/dev/null && pwd )"

DEPLOY_TAG="$(date +%Y%m%d%H%M%S)"

export AWS_PROFILE=westial

LAMBDA_FUNCTION="DuckDuckGoJumpAndReadRouter"
LAMBDA_JAR="jumpandread-0.2.jar"
HANDLER_CLASS="com.westial.alexa.jumpandread.DuckDuckGoJumpAndReadRouter"
MAIN_REGION="eu-west-1"
AWS_ACCOUNT="018128475095"
LAMBDA_ROLE="service-role/lambda"

cd "/home/jaume/workspace/alexa-jumpandread"

mvn --settings "/home/jaume/.m2/empty_settings.xml" package

if [[ $? -eq 0 ]]; then
    echo "SUCCESS: Package built"
else
    echo "ERROR: building package"
    exit 1
fi

aws s3api put-object \
    --bucket "com.westial.alexa.developer" \
    --key "build/$LAMBDA_JAR" \
    --body "target/$LAMBDA_JAR"

if [[ $? -eq 0 ]]; then
    echo "SUCCESS: Object uploaded to S3"
else
    echo "ERROR: Putting to S3"
    exit 1
fi

function deploy_lambda
{
    DEPLOY_REGION=$1
    DEPLOY_FUNCTION=$2
    DEPLOY_JAR=$3
    DEPLOY_HANDLER=$4
    DEPLOY_VARS_FILE=$5
    DEPLOY_AWS_ACCOUNT=$6
    ROLE=$7
    VERSION_TAG=$8

    DEPLOY_ARN="arn:aws:lambda:$DEPLOY_REGION:$DEPLOY_AWS_ACCOUNT:function:$DEPLOY_FUNCTION"
    DEPLOY_ROLE="arn:aws:iam::$DEPLOY_AWS_ACCOUNT:role/$ROLE"

    aws lambda update-function-configuration \
          --function-name "$DEPLOY_ARN" \
          --environment "Variables={}" \
          --region "$DEPLOY_REGION"

    if [[ $? -eq 0 ]]; then
        echo "SUCCESS: Reset Lambda configuration"
    else
        echo "ERROR: Reseting Lambda configuration"
        exit 1
    fi

    aws lambda update-function-configuration \
          --function-name "$DEPLOY_ARN" \
          --runtime "java8" \
          --description "Version $VERSION_TAG - $LAMBDA_JAR" \
          --handler "$DEPLOY_HANDLER" \
          --memory-size "512" \
          --role "$DEPLOY_ROLE" \
          --timeout "30" \
          --region "$DEPLOY_REGION" \
          --environment "file://$DEPLOY_VARS_FILE"

    if [[ $? -eq 0 ]]; then
        echo "SUCCESS: Updated Lambda configuration"
    else
        echo "ERROR: Updating Lambda configuration"
        exit 1
    fi

    aws lambda update-function-code \
        --function-name "$DEPLOY_ARN" \
        --s3-bucket "com.westial.alexa.developer" \
        --s3-key "build/$DEPLOY_JAR"

    if [[ $? -eq 0 ]]; then
        echo "SUCCESS: Updated Lambda code"
    else
        echo "ERROR: Updating Lambda"
        exit 1
    fi
}

deploy_lambda "$MAIN_REGION" "$LAMBDA_FUNCTION" "$LAMBDA_JAR" "$HANDLER_CLASS" "$SCRIPT_DIR/environment.es.json" "$AWS_ACCOUNT" "$LAMBDA_ROLE" "$DEPLOY_TAG"


if [[ $? -eq 0 ]]; then
    echo "SUCCESS: Updated full Lambda"
else
    echo "ERROR: Updating full Lambda"
    exit 1
fi

echo "TESTING..."

touch "/tmp/deploy_lambda.out"
chmod 0777 "/tmp/deploy_lambda.out"

aws lambda invoke \
    --function-name "arn:aws:lambda:$MAIN_REGION:$AWS_ACCOUNT:function:$LAMBDA_FUNCTION" \
    --payload "file://./deploy/tests/MyAlexaTest.json" \
    "/tmp/deploy_lambda.out"

cat "/tmp/deploy_lambda.out"

print ""

print "Deployed $DEPLOY_TAG tag finished at $(date)"

print ""

exit 0