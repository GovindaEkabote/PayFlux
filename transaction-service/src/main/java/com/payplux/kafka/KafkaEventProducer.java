package com.payplux.kafka;

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

    public KafkaEventProducer(KafkaTemplate<String, Transaction> kafkaTemplate) {
        this.kafkaTemplate = kafkaTemplate;
    }

    public void sendTransactionEvent(String key, Transaction transaction) {

        System.out.println("📤 Sending to Kafka → Topic: " + TOPIC +
                ", Key: " + key +
                ", Message: " + transaction);

        CompletableFuture<SendResult<String, Transaction>> future =
                kafkaTemplate.send(TOPIC, key, transaction);

        future.thenAccept(result -> {

            RecordMetadata metadata = result.getRecordMetadata();

            System.out.println("✅ Kafka message sent successfully!");
            System.out.println("📌 Topic: " + metadata.topic());
            System.out.println("📦 Partition: " + metadata.partition());
            System.out.println("📍 Offset: " + metadata.offset());
        });

        future.exceptionally(ex -> {
            System.err.println("❌ Failed to send Kafka message: " + ex.getMessage());
            ex.printStackTrace();
            return null;
        });
    }
}