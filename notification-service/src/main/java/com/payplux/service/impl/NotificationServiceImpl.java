package com.payplux.service.impl;

import com.payplux.model.Notification;
import com.payplux.model.NotificationStatus;
import com.payplux.repository.NotificationRepository;
import com.payplux.service.NotificationService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;


@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private final NotificationRepository notificationRepository;

    @Override
    public Notification sendNotification(Notification notification) {

        notification.setSentAt(LocalDateTime.now());

        if (notification.getStatus() == null) {
            notification.setStatus(NotificationStatus.SENT);
        }

        return notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotificationsByUserId(Long userId) {
        return notificationRepository.findByReceiverId(userId);
    }
}
