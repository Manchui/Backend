package com.manchui.domain.notification.service;

import com.manchui.domain.user.entity.User;
import com.manchui.domain.notification.dto.NotificationCursorPagingResponse;
import com.manchui.domain.notification.entity.NotificationType;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Service
public interface NotificationService {

    NotificationCursorPagingResponse getNotifications(String email, boolean unreadOnly, Long cursor, int size);

    void markAsRead(Long id, String email);

    void markAllAsRead(String email);

    void deleteNotification(Long id, String email);

    SseEmitter subscribe(String email, String lastEventId);

//    void deleteEmitter(String emitterId);

    void send(User receiver, NotificationType notificationType, String content, Long gatheringId);

}
