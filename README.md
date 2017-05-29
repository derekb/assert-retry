# Assert Retry

An extension to JUnit/Hamcrest providing _assertions with tolerance_, featuring a __retry__ mechanism.


## Motivation

AFAIK there are a couple of alternatives out there:
 
- [Awaitility](https://github.com/awaitility/awaitility)
- [guava-retrying](https://github.com/rholder/guava-retrying)

Awaitility in particular looks pretty good indeed.

The main added value of `assert-retry` is that it's modeled after JUnit/Hamcrest `Assert.assertThat`,
in terms of signature, and in terms of feedback to the user in case the assertion fails.

Who loves `Assert.assertThat` may not want to miss `AssertRetry.assertThat`!

Read on for a taste.


## Example of usage

Say that we have a JMS queue, and we need to verify that a message with body "expected content" is published on the queue.
Given the async nature of the system, we need to employ a bit of tolerance in our assertions.

    import static me.alb_i986.testing.assertions.AssertRetry.*;
      
    MessageConsumer consumer = session.createConsumer(queue);
    connection.start();
    Supplier<String> messageText = new Supplier<>() {
        @Override
        public String get() throws JMSException {
            TextMessage m = (TextMessage) consumer.receiveNoWait();  // polling for messages, non blocking
            return m == null ? null : m.getText();
        }
    };
    assertThat(messageText, eventually(containsString("expected content")),
            configureRetry()
                .maxAttempts(10)
                .sleepBetweenAttempts(5, TimeUnit.SECONDS);
                .retryOnException(true)
                .timeoutAfter(60, TimeUnit.SECONDS)
    );

The first few lines set up the supplier of actual values, which will be used to poll the message queue for messages.

BTW, it is recommended to extract the Supplier variable to a method, in order to help with code reuse.

Then we have our assertion method.
As you can see, it reads very much like a JUnit/Hamcrest `assertThat` assertion.
In this case it's asserting that the expected text message will be received within 10 attempts.
After each failing attempt, it will wait for 5s, and then try again.

If `consumer.receiveNoWait()` throws a `JMSException`, the assertion will be re-tried,
as if it returned a non-matching value.

Finally, the assertion will timeout after 60s, and an AssertionError similar to the following will be thrown:

       java.lang.AssertionError: Assertion failed after 10/10 attempts (49s):
           Expected: eventually a string containing "expected content"
           Actual values: (in order of appearance)
             - "some content"
             - null
             - "some other content"

For more info, please check the javadoc of `AssertRetry#assertThat`.
