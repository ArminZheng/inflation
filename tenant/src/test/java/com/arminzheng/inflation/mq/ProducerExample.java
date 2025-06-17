package com.arminzheng.inflation.mq;

import java.io.IOException;
import org.apache.rocketmq.client.apis.ClientConfiguration;
import org.apache.rocketmq.client.apis.ClientConfigurationBuilder;
import org.apache.rocketmq.client.apis.ClientException;
import org.apache.rocketmq.client.apis.ClientServiceProvider;
import org.apache.rocketmq.client.apis.message.Message;
import org.apache.rocketmq.client.apis.producer.Producer;
import org.apache.rocketmq.client.apis.producer.SendReceipt;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ProducerExample {
    private static final Logger log = LoggerFactory.getLogger(ProducerExample.class);

    public static void main(String[] args) throws ClientException, IOException {
        String endpoint = "localhost:8081";
        String topic = "TestTopic";
        ClientServiceProvider provider = ClientServiceProvider.loadService();
        ClientConfigurationBuilder builder = ClientConfiguration.newBuilder().setEndpoints(endpoint);
        ClientConfiguration configuration = builder.build();
        Producer producer = provider.newProducerBuilder()
            .setTopics(topic)
            .setClientConfiguration(configuration)
            .build();
        Message message = provider.newMessageBuilder()
            .setTopic(topic)
            .setKeys("messageKey")
            .setTag("messageTag")
            .setBody("messageBody".getBytes())
            .build();
        try {
            SendReceipt sendReceipt = producer.send(message);
            log.error("Send message successfully, messageId = {}", sendReceipt.getMessageId());
        } catch (ClientException e) {
            log.error("Failed to send message: {}", e.getMessage());
        }
        log.info("safely close");
        producer.close();
    }
}
