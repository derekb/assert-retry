# Assert Retry

An extension to JUnit/Hamcrest providing assertions with _tolerance_, featuring a __retry__ mechanism.

## Usage

    import me.alb_i986.testing.assertions.AssertRetry;
          
    MessageConsumer consumer = session.createConsumer(queue);
    connection.start();
    Supplier<String> messageText = new Supplier<>() {
      @Override
      public String get() throws JMSException {
         TextMessage m = (TextMessage) consumer.receiveNoWait();  // polling for messages, non blocking
         return m == null ? null : m.getText();
      }
    };
    RetryAssert.assertThat(messageText, eventually(containsString("expected content")),
           RetryConfig.builder()
               .withMaxAttempts(10)
               .withWaitStrategy(WaitStrategies.sleep(5, TimeUnit.SECONDS));
               .withRetryOnException(true)
               .build());
    