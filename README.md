Alexa Jump and Read
===================

**THIS PROJECT IS UNMAINTAINED AND SOME VULNERABILITIES IN THE DEPENDENCY CHAIN HAS BEEN FOUND.
IF YOU USE THIS PROJECT FOR ANYTHING YOU HAVE TO FIX THIS VULNERABILITIES AT LEAST IN YOUR FORK.
More information here <https://github.com/westial/jumpandread/security/dependabot>**

This is a stable working Alexa Skill source code with a deployment help script
used for a former unsuccessful business.

  * [My former business idea](#my-former-business-idea)
    + [I'm going to be rich](#im-going-to-be-rich)
    + [Developing, releasing, certification, start again](#developing-releasing-certification-start-again)
    + [What happened](#what-happened)
  * [This software](#this-software)
    + [You can easily start from here](#you-can-easily-start-from-here)
    + [Details](#details)
      - [Requirements](#requirements)
      - [/deploy](#deploy)
      - [/model](#model)
      - [/src](#src)

## My former business idea ##

### I'm going to be rich ###

After I attended two workshops about AWS Alexa and I spent a few hours reading
articles about how to program a Skill, I really believed that programming a Skill
I could start a plenty unattended business, something like dreaming that anyone
can be rich programming a mobile App. But in this case, voice services are quite
new and there is less competitors involved. I was wrong in many ways.

### Developing, releasing, certification, start again ###

I spent many days working on that Skill. Alexa is going to read the website 
while you are driving, or just resting. It was a huge work from BDD, and
finally I released my first version.

Oh... one of the main Alexa Skill policies tells that an opened websearch engine
is prohibited. I had to search only within a previously declared domains. That
made me spend a few hours more.

Finally I released and I got the skill certified limiting the search queries 
within one only domain, a very popular publishing platform. I released for
spanish first and english later. Spanish certification was quite easier than 
english one, much much more.

I got a mean of 2 or 3 users every week, and after a few months I only got two
user feedback, one worse and the other worst.

### What happened ###

Meanwhile the Skill was in production, I decided to deepen into AWS Alexa Skills.
Thus I realized that my skill approach is totally wrong. This skill works like
an automated phone service, and that's what Alexa guidelines want you keep out
of your mind when you are designing a skill.

I recommend to carefully follow ALL the "getting started" tutorials provided by
AWS before your first important skill, otherwise you are going to lose your
valued time.

## This software ##

### You can easily start from here ###

I made public this project because despite the Alexa Skill approach is totally
wrong, the architecture is highly maintainable, and may be one of you want to
resume the idea where I left it.

There are many abstractions where you can interfere with new implementations
according a new approach. Actually the wrong part is isolated along the 
AWS implementation and infrastructure layers only.

### Details ###

#### Requirements ####

* AWS Account and a user with programmatic access to use the deployment script.
* AWS IAM Role with permissions to execute Lambda Functions and handling 
DynamoDB records.
* An Alexa Skill created through the Alexa Skill console.

#### /deploy ####

Deployment script consist on an AWS Lambda function update and remote test 
and a DynamoDB tables creation command. 

You have to provide an AWS account, a role allowed to execute functions and 
handle DynamoDB records. That's not a magic installation and deployment script.
It helped me a lot because upgrading the AWS Lambda by hand, in the console, is
quite tedious.

* /deploy/conf/global.sh.template: copy this file into a new global.sh file and 
configure the parameters before deployment execution.
* /deploy/deploy_lambda.sh: Deployment script for building, uploading and 
configuring the AWS Lambda function.

#### /model ####

AWS Alexa Skill model.

#### /src ####

Java 8 source code with maven POM file dependencies management.

* /src/main/java/com/westial/alexa/jumpandread: production source code.
* /src/test/features: behavioural gherkin tests with no integration.
* /src/test/java/junit: JUNIT based integration tests.
