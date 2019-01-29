# aws_sqs

Testing the functionality of the SQS Service

Core 3 functions which are being used in a future project are being covered
For later enhancements, long polling will be tested in order to improve performance when reading messages with larger
response timeouts

Usage:

Run with:

mvn clean test

To run either test include the relevant tags for the Scenario (@send, @sendMultiple, @deadLetter)

mvn clean test -Dcucumber.options="--tags @send"

