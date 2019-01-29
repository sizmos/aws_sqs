Feature: Testing the functionality of the SQS Service

  @send
  Scenario Outline: Sending a message to an SQS queue
    Given I have setup my SQS Queue with the following name <queue_name>
    When I send a message with the following text <message_text>
    Then I should receive a response at the queue <queue_name> with the following text <message_text>
    And I can remove my queues and confirm they are deleted
    Examples:
      | queue_name | message_text            |
      | queue1     | This is a test Message! |

  @sendMultiple
  Scenario Outline: Sending multiple message to an SQS queue
    Given I have setup my SQS Queue with the following name <queue_name>
    When I send multiple messages with the following text <message_text_1> and <message_text_2>
    Then I should receive a response at the queue <queue_name> with the following text <message_text_2>
    And I purge the messages from the queue
    Then I should not receive a response at the queue <queue_name>
    And I can remove my queues and confirm they are deleted
    Examples:
      | queue_name | message_text_1          | message_text_2                |
      | queue1     | This is a test Message! | This is another test Message! |

  @deadLetter
  Scenario Outline: Verifying Dead Letter Queue Functionality
    Given I have setup my SQS Queue with the following name <queue_name>
    And I have setup my SQS Dead letter Queue with the following name <dead_letter_queue_name>
    When I send a message with the following text <message_text>
    Then I should receive a response at the queue <queue_name> with the following text <message_text>
    And I wait 15 seconds for my message to expire
    Then I should not receive a response at the queue <queue_name>
    Then I should receive a response at the queue <dead_letter_queue_name> with the following text <message_text>
    Then I should not receive a response at the queue <queue_name>
    And I can remove my queues and confirm they are deleted
    Examples:
      | queue_name | dead_letter_queue_name | message_text           |
      | queue1     | deadQueue1             | This is a test Message! |

