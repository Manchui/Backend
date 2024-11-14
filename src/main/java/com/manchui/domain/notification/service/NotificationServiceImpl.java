package com.manchui.domain.notification.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import com.manchui.domain.entity.User;
import com.manchui.domain.notification.dto.NotificationCursorPagingResponse;
import com.manchui.domain.notification.dto.NotificationResponse;
import com.manchui.domain.notification.entity.Notification;
import com.manchui.domain.notification.entity.NotificationContent;
import com.manchui.domain.notification.entity.NotificationType;
import com.manchui.domain.notification.repository.EmitterRepository;
import com.manchui.domain.notification.repository.NotificationRepository;
import com.manchui.domain.service.UserService;
import com.manchui.global.exception.CustomException;
import com.manchui.global.exception.ErrorCode;
import com.manchui.global.response.SuccessResponse;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.manchui.global.response.SuccessResponse.successSseWithData;

@Slf4j
@Service
@RequiredArgsConstructor
public class NotificationServiceImpl implements NotificationService {

    private static final String SSE = "SSE";
    private static final String SERVICE = "NotificationServiceImpl";
    private static final String UNHANDLED_SERVER_ERROR = "Unhandled server error occurred.";

    private final UserService userService;
    private final EmitterRepository emitterRepository;
    private final NotificationRepository notificationRepository;

    private final ObjectMapper objectMapper = new ObjectMapper()
            .registerModule(new JavaTimeModule())
            .disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS); // ISO-8601 포맷 사용

    @Value("${token.access.expiration}")
    private Long accessTokenExpiration;

    @Override
    public NotificationCursorPagingResponse getNotifications(String email, boolean unreadOnly, Long cursor, int size) {

        log.info("사용자 {}의 알림 목록 조회 시작", email);

        userService.checkUser(email);

        // 커서 기반 최신순 정렬
        Sort sort = Sort.by(Sort.Order.desc("createdAt"));
        PageRequest pageRequest = PageRequest.of(0, size, sort);

        Page<Notification> notifications = fetchNotificationsByCursor(email, unreadOnly, cursor, pageRequest);

        log.info("받은 알림 개수 : {}", notifications.getTotalElements());

        Long nextCursor = notifications.isEmpty() ? null : notifications.getContent().get(notifications.getNumberOfElements() - 1).getId();

        return new NotificationCursorPagingResponse(
                countNotifications(email, unreadOnly),
                notifications.getContent().stream()
                        .map(NotificationResponse::fromEntity)
                        .collect(Collectors.toList()),
                nextCursor
        );
    }

    private Page<Notification> fetchNotificationsByCursor(String email, boolean unreadOnly, Long cursor, PageRequest pageRequest) {

        if (cursor != null) {
            if (unreadOnly) {
                return notificationRepository.findByReceiverEmailAndIsReadFalseAndDeletedAtIsNullAndIdLessThan(email, cursor, pageRequest);
            } else {
                return notificationRepository.findByReceiverEmailAndDeletedAtIsNullAndIdLessThan(email, cursor, pageRequest);
            }
        } else {
            if (unreadOnly) {
                return notificationRepository.findByReceiverEmailAndIsReadFalseAndDeletedAtIsNull(email, pageRequest);
            } else {
                return notificationRepository.findByReceiverEmailAndDeletedAtIsNull(email, pageRequest);
            }
        }
    }

    private int countNotifications(String email, boolean unreadOnly) {

        if (unreadOnly) {
            return notificationRepository.countByReceiverEmailAndIsReadFalseAndDeletedAtIsNull(email);
        } else {
            return notificationRepository.countByReceiverEmailAndDeletedAtIsNull(email);
        }
    }

    @Override
    @Transactional
    public void markAsRead(Long id, String email) {

        log.info("알림 id {}를 읽음 처리로 변경합니다.", id);

        userService.checkUser(email);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        validateNotification(notification, email, false);

        notification.read();

        log.info("알림 id {}를 읽은 상태로 변경하였습니다.", id);
    }

    @Override
    @Transactional
    public void markAllAsRead(String email) {

        log.info("알림 일괄 읽음 처리 시작");

        User user = userService.checkUser(email);

        List<Notification> notificationList = notificationRepository.findAllByReceiver(user);

        notificationList.forEach(Notification::read);

        log.info("알림 일괄 읽음 처리 완료");
    }

    @Override
    @Transactional
    public void deleteNotification(Long id, String email) {

        log.info("알림 id {}를 삭제 처리합니다.", id);

        userService.checkUser(email);

        Notification notification = notificationRepository.findById(id)
                .orElseThrow(() -> new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND));

        validateNotification(notification, email, true);

        notification.softDelete();

        log.info("알림 id {}가 삭제 처리되었습니다.", id);
    }

    @Override
    public SseEmitter subscribe(String email, String lastEventId) {

        String emitterId = emitterRepository.findAllEmitterIdsStartWith(email).stream()
                .findFirst()
                .orElseGet(() -> generateEmitterId(email)); // 기존 EmitterId가 없으면 새로 생성

        SseEmitter emitter = emitterRepository.findById(emitterId)
                .orElseGet(() -> {
                    SseEmitter newEmitter = new SseEmitter(accessTokenExpiration);
                    emitterRepository.save(emitterId, newEmitter);
                    log.info("새로운 Emitter 생성 및 저장 완료 - emitterId: {}", emitterId);
                    return newEmitter;
                });

        setEmitterCompletionHandlers(emitter, emitterId);

        try {
            // 초기 연결 메시지 및 이전 알림 전송
            sendInitialSseMessage(email, emitterId, emitter);
            sendNotificationsFromLastEventId(email, lastEventId, emitter);
        } catch (IOException e) {
            log.error("SSE 연결 실패 - emitterId: {}", emitterId, e);
            emitterRepository.deleteById(emitterId);
            handleEmitterError(e, email, emitter);
//            throw new InvalidRequestException(SSE, SERVICE, UNHANDLED_SERVER_ERROR);
        }

        return emitter;
    }

    private String generateEmitterId(String email) {

        return email + "_" + System.currentTimeMillis();
    }

    private void setEmitterCompletionHandlers(SseEmitter emitter, String emitterId) {

        emitter.onCompletion(() -> {
            log.info("Emitter 완료 - emitterId: {}", emitterId);
            emitterRepository.deleteById(emitterId);
        });
        emitter.onTimeout(() -> {
            log.info("Emitter 타임아웃 - emitterId: {}", emitterId);
            emitterRepository.deleteById(emitterId);
        });
        emitter.onError(e -> {
            log.warn("Emitter 에러 발생 - emitterId: {}", emitterId, e);
            emitterRepository.deleteById(emitterId);
        });
    }

    private void sendInitialSseMessage(String email, String emitterId, SseEmitter emitter) throws IOException {

        SuccessResponse<String> response = successSseWithData(email);
        String jsonResponse = objectMapper.writeValueAsString(response);

        emitter.send(SseEmitter.event()
                .id(emitterId)
//                .name("sse")
                .data(jsonResponse));

        log.info("SSE 연결 성공 메시지 전송 - emitterId: {}", emitterId);
    }

    private void sendNotificationsFromLastEventId(String email, String lastEventId, SseEmitter emitter) {

        if (lastEventId != null && !lastEventId.isEmpty()) {
            log.info("Last-Event-ID 기반 이벤트 전송 시작 - email: {}, Last-Event-ID: {}", email, lastEventId);

            Map<String, List<Notification>> events = emitterRepository.findAllEventCacheStartWithByEmail(email);
            events.entrySet().stream()
                    .filter(entry -> entry.getKey().compareTo(lastEventId) > 0)
                    .forEach(entry -> sendToClient(emitter, entry.getKey(), entry.getValue().get(0)));

            log.info("Last-Event-ID 기반 이벤트 전송 완료 - email: {}", email);
        }
    }

    @Override
    public void send(User receiver, NotificationType notificationType, String content, Long gatheringId) {

        log.info("알림 요청 생성");

        Notification notification = notificationRepository.save(createNotification(receiver, notificationType, content, gatheringId));
        log.info("알림 저장 완료");

        String email = receiver.getEmail();
        log.info("알림 받는 사람의 email : {}", email);

        Map<String, SseEmitter> sseEmitters = emitterRepository.findAllEmitterStartWithByEmail(email);
        log.info("Emitters 조회 결과 - email: {}, result: {}", email, sseEmitters);

        sseEmitters.forEach((key, emitter) -> {
            log.info("이벤트 캐시에 알림 저장");
            emitterRepository.saveEventCache(key, notification); // 이벤트 캐시에 알림 저장

            log.info("send()에서 sendToClient 호출 - key: {}, notification: {}", key, notification);

            sendToClient(emitter, key, notification);
        });
    }

    @Transactional
    private Notification createNotification(User receiver, NotificationType notificationType, String content, Long gatheringId) {

        log.info("알림 객체 생성");

        return Notification.builder()
                .receiver(receiver)
                .notificationType(notificationType)
                .content(new NotificationContent(content)) // Embedded 타입 생성
                .gatheringId(gatheringId)
                .isRead(false) // 읽음 상태 초기화
                .build();
    }

    private void sendToClient(SseEmitter emitter, String emitterId, Notification notification) {

        try {
            // 알림 JSON 변환
            String jsonResponse = objectMapper.writeValueAsString(Notification.NotificationResponseMapper.mapToResponse(notification));

            // 클라이언트로 알림 전송
            emitter.send(SseEmitter.event()
                    .id(notification.getId().toString())
//                    .name("notification")
                    .data(jsonResponse));

            log.info("클라이언트로 알림 전송 성공. 알림 ID: {}", notification.getId());
        } catch (IOException e) {
            log.error("클라이언트로 알림 전송 실패. emitterId: {} - {}", emitterId, e.getMessage());
            if (e.getMessage().contains("Broken pipe")) {
                log.warn("Broken pipe 예외 발생. Emitter 정리 중...");
            }
            emitter.completeWithError(e); // Emitter 종료 처리

            synchronized (this) {
                emitterRepository.deleteById(emitterId);
                emitterRepository.saveEventCache(emitterId, notification); // 이벤트 캐시 저장
            }

            log.info("Broken pipe 처리 후 알림 캐시 저장 완료. 알림 ID: {}", notification.getId());
        } catch (IllegalStateException e) {
            log.error("비정상적 Emitter 상태. 작업 중단.", e);
        }
    }

    private void validateNotification(Notification notification, String email, boolean isDeleteAction) {

        if (!notification.getReceiver().getEmail().equals(email)) {
            throw new CustomException(ErrorCode.NOTIFICATION_NOT_FOUND);
        }

        if (notification.getDeletedAt() != null) {
            throw new CustomException(ErrorCode.NOTIFICATION_ALREADY_DELETED);
        }

        if (isDeleteAction && notification.getIsRead()) {
            throw new CustomException(ErrorCode.NOTIFICATION_CANNOT_BE_DELETED);
        }
    }

    private void handleEmitterError(Throwable e, String email, SseEmitter emitter) {

        if (e instanceof IOException && "Broken pipe".equals(e.getMessage())) {
            // Broken pipe 발생 시, 새로운 SseEmitter 생성 및 재연결 시도
            log.info("Broken pipe detected, reconnecting emitter for email: {}", email);
            emitterRepository.deleteAllEmitterStartWithEmail(email);
            // 새로운 Emitter 생성 및 저장
            SseEmitter newEmitter; // 새로운 emitter 생성
            String emitterId = generateEmitterId(email);
            newEmitter = emitterRepository.save(emitterId, new SseEmitter(accessTokenExpiration));

            newEmitter.onCompletion(() -> emitterRepository.deleteAllEventCacheStartWithEmail(email));
            newEmitter.onError((newError) -> handleEmitterError(newError, email, newEmitter));
        } else {
            log.error("Emitter error occurred: {}", e.getMessage());
            emitterRepository.deleteAllEventCacheStartWithEmail(email);
        }
    }

}
