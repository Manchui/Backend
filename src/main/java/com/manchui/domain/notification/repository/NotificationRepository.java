package com.manchui.domain.notification.repository;

import com.manchui.domain.user.entity.User;
import com.manchui.domain.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    // 알림 개수 계산 메서드 (cursor 조건 없이 사용)
    int countByReceiverEmailAndIsReadFalseAndDeletedAtIsNull(String email);

    int countByReceiverEmailAndDeletedAtIsNull(String email);

    // 읽지 않은 알림 중 cursor보다 작은 ID 조회
    Page<Notification> findByReceiverEmailAndIsReadFalseAndDeletedAtIsNullAndIdLessThan(String email, Long cursor, Pageable pageable);

    // 모든 알림 중 cursor보다 작은 ID 조회
    Page<Notification> findByReceiverEmailAndDeletedAtIsNullAndIdLessThan(String email, Long cursor, Pageable pageable);

    // 읽지 않은 알림 조회
    Page<Notification> findByReceiverEmailAndIsReadFalseAndDeletedAtIsNull(String email, Pageable pageable);

    // 모든 알림 조회
    Page<Notification> findByReceiverEmailAndDeletedAtIsNull(String email, Pageable pageable);

    List<Notification> findAllByReceiver(User receiver);

    List<Notification> findAllByReceiverAndIdGreaterThan(User user, long parseLong);

}
