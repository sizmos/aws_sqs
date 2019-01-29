package com.awssqs.steps;

import com.awssqs.SQSObject;
import cucumber.api.java.en.And;
import cucumber.api.java.en.Given;
import cucumber.api.java.en.Then;
import cucumber.api.java.en.When;
import org.junit.Assert;

public class SQSTestSteps {

    SQSObject mySQS;

    @Given("^I have setup my SQS Queue with the following name (.*?)$")
    public void initialiseSQSQueue(String queueName) throws Throwable {
        System.out.println("Initialising the SQS Queue");
        mySQS = new SQSObject();
        mySQS.initSQSQueue(queueName);
        System.out.println("Queue has been Initialised!");
    }

    @And("^I have setup my SQS Dead letter Queue with the following name (.*?)$")
    public void initialiseSQSDeadLetterQueue(String queueName) throws Throwable {
        System.out.println("Initialising the SQS Dead Letter Queue");
        mySQS.initDeadLetterQueue(queueName);
        System.out.println("Dead Letter Queue has been Initialised!");
    }

    @When("^I send a message with the following text (.*?)$")
    public void sendMessageToSQSQueue(String messageText) throws Throwable {
        System.out.println("Sending a message to the SQS Queue");
        mySQS.sendMessage(messageText);
        System.out.println("Message has been sent: " + messageText);
    }

    @When("^I send multiple messages with the following text (.*?) and (.*?)$")
    public void sendMultipleMessagesToSQSQueue(String message1Text, String message2Text) throws Throwable {
        System.out.println("Sending a message to the SQS Queue");
        mySQS.sendMultipleMessages(message1Text, message2Text);
        System.out.println("Messages have been sent: " + message1Text + "\n" );
    }

    @Then("^I should receive a response at the queue (.*?) with the following text (.*?)$")
    public void receiveMessageFromSQSQueue(String queueName, String messageText) throws Throwable {
        System.out.println("Receiving a message from the SQS Queue");
        String message = mySQS.receiveMessage(queueName).get(0).getBody();
        Assert.assertTrue("Message is Incorrect: " + message, message.equals(messageText));
        System.out.println("Message has been received: " + message);
    }

    @Then("^I should not receive a response at the queue (.*)$")
    public void notReceiveMessageFromSQSQueue(String queueName) throws Throwable {
        Assert.assertTrue("Messages are Still present!", mySQS.receiveMessage(queueName).isEmpty());
        System.out.println("Messages have been cleared successfully!");
    }

    @And("^I purge the messages from the queue$")
    public void purgeQueueMessages() throws Throwable {
        mySQS.purgeQueue();
        System.out.println("Queue has been purged!");
    }

    @And("^I can remove my queues and confirm they are deleted$")
    public void deleteQueueAndConfirmRemoval() throws Throwable {
        mySQS.deleteQueues();

        Assert.assertTrue("Queues were not deleted!", mySQS.showQueues().isEmpty());
        System.out.println("Queues have been deleted!");
    }

    @And("^I wait (.*) seconds for my message to expire$")
    public void waitForMessageToExpire(int time) throws Throwable {
        System.out.println("waiting " + time + " seconds!");
        Thread.sleep(time * 1000);
    }
}
