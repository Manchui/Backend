package com.manchui.domain.notification.dto;

import com.manchui.domain.notification.entity.Notification;
import com.manchui.domain.notification.entity.NotificationType;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Getter;

import java.time.LocalDateTime;

@Schema(description = "알림 응답")
@Getter
public class NotificationResponse {

    private final Long notificationId;
    private final String content; // String으로 변경
    private final Long gatheringId;
    private final NotificationType notificationType;
    private final Boolean isRead;
    private final LocalDateTime createdAt;

    public NotificationResponse(Long id, String content, Long gatheringId, NotificationType notificationType, Boolean isRead, LocalDateTime createdAt) {

        this.notificationId = id;
        this.content = content;
        this.gatheringId = gatheringId;
        this.notificationType = notificationType;
        this.isRead = isRead;
        this.createdAt = createdAt;
    }

    public static NotificationResponse fromEntity(Notification notification) {

        return new NotificationResponse(
                notification.getId(),
                notification.getContent() != null ? notification.getContent().getContent() : null, // 객체에서 문자열로 변환
                notification.getGatheringId(),
                notification.getNotificationType(),
                notification.getIsRead(),
                notification.getCreatedAt()
        );
    }

}
