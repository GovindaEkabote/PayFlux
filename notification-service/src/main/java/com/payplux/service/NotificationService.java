package com.payplux.service;

import com.payplux.model.Notification;

import java.util.List;

public interface NotificationService {

    Notification sendNotification(Notification notification);
    List<Notification> getNotificationsByUserId(Long userId);


}
