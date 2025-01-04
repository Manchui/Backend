package com.manchui.domain.notification.repository;

import com.manchui.domain.notification.entity.Notification;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface EmitterRepository {

    // 새로운 SseEmitter 저장
    SseEmitter save(String emitterId, SseEmitter sseEmitter);

    // 특정 emitterId의 이벤트 캐시 가져오기
    List<Notification> getEventCache(String emitterId);

    // 특정 emitterId에 이벤트 저장
    void saveEventCache(String emitterId, Notification notification);

    // 이메일로 시작하는 모든 SseEmitter를 반환
    Map<String, SseEmitter> findAllEmitterStartWithByEmail(String email);

    // 이메일로 시작하는 모든 이벤트 캐시 반환
    Map<String, List<Notification>> findAllEventCacheStartWithByEmail(String email);

    // emitterId에 해당하는 SseEmitter 삭제
    void deleteById(String emitterId);

    // 이메일로 시작하는 모든 SseEmitter 삭제
    void deleteAllEmitterStartWithEmail(String email);

    // 이메일로 시작하는 모든 이벤트 캐시 삭제
    void deleteAllEventCacheStartWithEmail(String email);

    boolean existsById(String emitterId);

    List<String> findAllEmitterIdsStartWith(String email);

    Optional<SseEmitter> findById(String emitterId);

    void removeOldEmitters(long maxAgeMillis);

}
