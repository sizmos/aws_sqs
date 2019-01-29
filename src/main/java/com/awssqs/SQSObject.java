package com.awssqs;

import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.client.builder.AwsClientBuilder;
import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.AmazonSQSClientBuilder;
import com.amazonaws.services.sqs.model.*;

import java.io.FileInputStream;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

public class SQSObject {

    String endpoint;
    String region;
    String accessKey;
    String secretKey;

    AmazonSQS client;
    String queueUrl;
    String deadLetterQueueUrl;

    public void initSQSQueue(String queueName) throws InterruptedException {

        Properties properties = new Properties();
        InputStream input;
        try {
            input = new FileInputStream("src/main/resources/sqstest.properties");
            properties.load(input);
        } catch (Exception e) {
            e.printStackTrace();
        }

        endpoint = properties.getProperty("sqs.url");
        region = properties.getProperty("sqs.region");
        accessKey = properties.getProperty("sqs.accessKey");
        secretKey = properties.getProperty("sqs.secretKey");

        client = AmazonSQSClientBuilder.standard()
                .withCredentials(new AWSStaticCredentialsProvider(new BasicAWSCredentials(accessKey, secretKey)))
                .withEndpointConfiguration(new AwsClientBuilder.EndpointConfiguration(endpoint, region))
                .build();

        CreateQueueRequest createRequest = new CreateQueueRequest(queueName)
                .addAttributesEntry("MessageRetentionPeriod", "10")
                .addAttributesEntry("VisibilityTimeout", "10");

        try {
            client.createQueue(createRequest);
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equalsIgnoreCase("QueueAlreadyExists")) {
                throw e;
            }
        }

        // Get the URL for a queue
        queueUrl = client.getQueueUrl(queueName).getQueueUrl();
    }

    public void initDeadLetterQueue(String queueName) throws InterruptedException {
        try {
            client.createQueue(queueName);
        } catch (AmazonSQSException e) {
            if (!e.getErrorCode().equalsIgnoreCase("QueueAlreadyExists")) {
                throw e;
            }
        }

        deadLetterQueueUrl = client.getQueueUrl(queueName).getQueueUrl();

        GetQueueAttributesResult queue_attrs = client.getQueueAttributes(
                new GetQueueAttributesRequest(deadLetterQueueUrl)
                        .withAttributeNames("QueueArn"));

        String dl_queue_arn = queue_attrs.getAttributes().get("QueueArn");

        SetQueueAttributesRequest request = new SetQueueAttributesRequest()
                .withQueueUrl(queueUrl)
                .addAttributesEntry("RedrivePolicy",
                        "{\"maxReceiveCount\":\"1\", \"deadLetterTargetArn\":\""
                                + dl_queue_arn + "\"}");

        client.setQueueAttributes(request);
    }

    public void sendMessage(String message) {
        SendMessageRequest send_msg_request = new SendMessageRequest()
                .withQueueUrl(queueUrl)
                .withMessageBody(message)
                .withDelaySeconds(0);
        client.sendMessage(send_msg_request);
    }

    public void sendMultipleMessages(String message1, String message2) {
        SendMessageBatchRequest send_batch_request = new SendMessageBatchRequest()
                .withQueueUrl(queueUrl)
                .withEntries(
                        new SendMessageBatchRequestEntry(
                                "msg_1", message1),
                        new SendMessageBatchRequestEntry(
                                "msg_2", message2)
                                .withDelaySeconds(0));
        client.sendMessageBatch(send_batch_request);
    }

    public List<Message> receiveMessage(String queueName) {
        // receive messages from the queue
        List<Message> messages = client.receiveMessage(client.getQueueUrl(queueName).getQueueUrl()).getMessages();

        return messages;
    }

    public List<String> showQueues() {
        List<String> queues = client.listQueues().getQueueUrls();
        System.out.println("Your SQS Queue URLs:");
        for (String url : queues) {
            System.out.println(url);
        }
        return queues;
    }

    public void deleteQueues() {
        List<String> queues = client.listQueues().getQueueUrls();
        for (String url : queues) {
            client.deleteQueue(url);
        }
    }

    public void purgeQueue() {
        List<Message> messages = client.receiveMessage(queueUrl).getMessages();

        // delete messages from the queue
        for (Message m : messages) {
            client.deleteMessage(queueUrl, m.getReceiptHandle());
        }
    }
}
