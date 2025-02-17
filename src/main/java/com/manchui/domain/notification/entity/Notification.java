package com.manchui.domain.notification.entity;

import com.manchui.domain.user.entity.User;
import com.manchui.domain.notification.dto.NotificationResponse;
import com.manchui.global.entity.Timestamped;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

@Table(name = "notification")
@Entity
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends Timestamped {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Embedded
    private NotificationContent content;

    @Column(nullable = false)
    private Long gatheringId;

    @Column(nullable = false)
    private Boolean isRead;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private NotificationType notificationType;

    @OnDelete(action = OnDeleteAction.CASCADE)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User receiver;

    public void read() {

        isRead = true;
    }

    public static class NotificationResponseMapper {

        public static NotificationResponse mapToResponse(Notification notification) {

            return new NotificationResponse(
                    notification.getId(),
                    notification.getContent() != null ? notification.getContent().getContent() : null,
                    notification.getGatheringId(),
                    notification.getNotificationType(),
                    notification.getIsRead(),
                    notification.getCreatedAt()
            );
        }

    }

}
