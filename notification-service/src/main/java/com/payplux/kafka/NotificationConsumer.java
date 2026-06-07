package com.payplux.kafka;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.payplux.model.Notification;
import com.payplux.model.NotificationStatus;
import com.payplux.model.Transaction;
import com.payplux.repository.NotificationRepository;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class NotificationConsumer {

    private final NotificationRepository notificationRepository;
    private final ObjectMapper mapper;

    public NotificationConsumer(NotificationRepository notificationRepository) {
        this.notificationRepository = notificationRepository;

        // ✅ Proper ObjectMapper setup (JavaTime support)
        this.mapper = new ObjectMapper();
        this.mapper.registerModule(new JavaTimeModule());
        this.mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
    }

    @KafkaListener(topics = "txn-initiated", groupId = "notification-group")
    public void consumeTransaction(Transaction transaction) {

        try {
            System.out.println("\n================ KAFKA EVENT RECEIVED ================");

            // Safe logging using ObjectMapper (better debugging)
            System.out.println("📥 Raw Transaction Event:");
            System.out.println(mapper.writeValueAsString(transaction));

            if (transaction == null) {
                System.out.println("❌ Transaction is NULL");
                return;
            }

            // 🔥 Build Notification
            Notification notification = new Notification();

            notification.setSenderId(transaction.getSenderId());
            notification.setReceiverId(transaction.getReceiverId());
            notification.setAmount(transaction.getAmount());

            notification.setStatus(NotificationStatus.SENT);
            notification.setSentAt(LocalDateTime.now());

            // 💾 Save to DB
            Notification saved = notificationRepository.save(notification);

            // ✅ Success logs
            System.out.println("✅ Notification saved successfully");
            System.out.println("🆔 Notification ID: " + saved.getId());
            System.out.println("📩 Sent to user: " + saved.getReceiverId());

            System.out.println("====================================================\n");

        } catch (Exception e) {

            System.out.println("❌ ERROR while processing Kafka message: " + e.getMessage());
            e.printStackTrace();
        }
    }
}