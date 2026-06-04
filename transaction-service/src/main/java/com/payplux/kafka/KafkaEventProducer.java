package com.payplux.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.payplux.model.Transaction;
import org.apache.kafka.clients.producer.RecordMetadata;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;

@Service
public class KafkaEventProducer {

    private static final String TOPIC = "txn-initiated";

    private final KafkaTemplate<String, Transaction> kafkaTemplate;
    private final ObjectMapper objectMapper;

    public KafkaEventProducer(KafkaTemplate<String, Transaction> kafkaTemplate,
                              ObjectMapper objectMapper) {
        this.kafkaTemplate = kafkaTemplate;
        this.objectMapper = objectMapper;
        this.objectMapper.registerModule(new JavaTimeModule());
    }

    public void sendTransactionEvent(String key, Transaction transaction) {

        System.out.println(
                "📤 Sending to Kafka → Topic: " + TOPIC +
                        ", Key: " + key +
                        ", Message: " + transaction);

        CompletableFuture<SendResult<String, Transaction>> future =
                kafkaTemplate.send(TOPIC, key, transaction);

        future.thenAccept(result -> {
            RecordMetadata metadata = result.getRecordMetadata();

            System.out.println(
                    "✅ Kafka message sent successfully! Topic: " + metadata.topic()
                            + ", Partition: " + metadata.partition()
                            + ", Offset: " + metadata.offset());

        }).exceptionally(ex -> {
            System.err.println("❌ Failed to send Kafka message: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        });
    }
}