package com.manchui.domain.notification.repository;

import com.manchui.domain.notification.entity.Notification;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@Slf4j
@Component
public class EmitterRepositoryImpl implements EmitterRepository {

    // 클라이언트와 서버 간의 SSE 연결을 저장하는 맵
    private final Map<String, SseEmitter> emitters = new ConcurrentHashMap<>();

    // 알림 데이터를 캐싱하는 맵 (Emitter ID를 기준으로 알림 목록 저장)
    private final Map<String, List<Notification>> eventCache = new ConcurrentHashMap<>();

    // 각 Emitter의 생성 시간을 저장하는 맵 (Emitter ID를 기준으로 타임스탬프 저장)
    private final Map<String, Long> timestamps = new ConcurrentHashMap<>();

    /**
     * 특정 Emitter ID에 알림 데이터를 추가하여 캐싱합니다.
     *
     * @param emitterId    Emitter의 고유 ID
     * @param notification 캐싱할 알림 데이터
     */
    @Override
    public void saveEventCache(String emitterId, Notification notification) {

        eventCache.computeIfAbsent(emitterId, k -> new ArrayList<>()).add(notification);
    }

    /**
     * 특정 Emitter ID에 해당하는 캐싱된 알림 데이터를 가져옵니다.
     *
     * @param emitterId Emitter의 고유 ID
     * @return 캐싱된 알림 목록
     */
    @Override
    public List<Notification> getEventCache(String emitterId) {

        return eventCache.getOrDefault(emitterId, new ArrayList<>());
    }

    /**
     * 특정 Emitter ID에 해당하는 캐싱된 알림 데이터를 삭제합니다.
     *
     * @param emitterId Emitter의 고유 ID
     */
    public void clearEventCache(String emitterId) {

        eventCache.remove(emitterId);
    }

    /**
     * 새로운 SSE 연결을 저장합니다.
     *
     * @param emitterId  Emitter의 고유 ID
     * @param sseEmitter 저장할 SseEmitter 객체
     * @return 저장된 SseEmitter 객체
     */
    @Override
    public SseEmitter save(String emitterId, SseEmitter sseEmitter) {

        emitters.put(emitterId, sseEmitter);
        timestamps.put(emitterId, System.currentTimeMillis()); // 생성 시간 저장
        return sseEmitter;
    }

    /**
     * 특정 이메일로 시작하는 Emitter 목록을 반환합니다.
     *
     * @param email 이메일 (키 값의 접두사)
     * @return 이메일로 시작하는 Emitter 맵
     */
    @Override
    public Map<String, SseEmitter> findAllEmitterStartWithByEmail(String email) {

        return emitters.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(email))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 특정 이메일로 시작하는 알림 캐시 목록을 반환합니다.
     *
     * @param email 이메일 (키 값의 접두사)
     * @return 이메일로 시작하는 알림 캐시 맵
     */
    @Override
    public Map<String, List<Notification>> findAllEventCacheStartWithByEmail(String email) {

        return eventCache.entrySet().stream()
                .filter(entry -> entry.getKey().startsWith(email))
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    /**
     * 특정 Emitter ID에 해당하는 SSE 연결을 삭제합니다.
     *
     * @param emitterId Emitter의 고유 ID
     */
    @Override
    public void deleteById(String emitterId) {

        emitters.remove(emitterId);
        timestamps.remove(emitterId); // 생성 시간도 삭제
    }

    /**
     * 특정 이메일로 시작하는 모든 SSE 연결을 삭제합니다.
     *
     * @param email 이메일 (키 값의 접두사)
     */
    @Override
    public void deleteAllEmitterStartWithEmail(String email) {

        emitters.keySet().removeIf(key -> key.startsWith(email));
        timestamps.keySet().removeIf(key -> key.startsWith(email)); // 타임스탬프도 함께 삭제
    }

    /**
     * 특정 이메일로 시작하는 모든 알림 캐시를 삭제합니다.
     *
     * @param email 이메일 (키 값의 접두사)
     */
    @Override
    public void deleteAllEventCacheStartWithEmail(String email) {

        eventCache.keySet().removeIf(key -> key.startsWith(email));
    }

    /**
     * 특정 Emitter ID가 존재하는지 확인합니다.
     *
     * @param emitterId Emitter의 고유 ID
     * @return 존재 여부
     */
    @Override
    public boolean existsById(String emitterId) {

        return emitters.containsKey(emitterId);
    }

    /**
     * 특정 이메일로 시작하는 모든 Emitter ID 목록을 반환합니다.
     *
     * @param email 이메일 (키 값의 접두사)
     * @return 이메일로 시작하는 Emitter ID 목록
     */
    @Override
    public List<String> findAllEmitterIdsStartWith(String email) {

        return emitters.keySet().stream()
                .filter(emitterId -> emitterId.startsWith(email))
                .collect(Collectors.toList());
    }

    /**
     * 특정 Emitter ID에 해당하는 SSE 연결을 가져옵니다.
     *
     * @param emitterId Emitter의 고유 ID
     * @return SSE 연결(Optional)
     */
    @Override
    public Optional<SseEmitter> findById(String emitterId) {

        return Optional.ofNullable(emitters.get(emitterId));
    }

    /**
     * 특정 시간 이상 오래된 SSE 연결을 제거합니다.
     *
     * @param maxAgeMillis 최대 허용 시간(밀리초)
     */
    @Override
    public void removeOldEmitters(long maxAgeMillis) {

        long currentTime = System.currentTimeMillis();
        timestamps.entrySet().removeIf(entry -> {
            if ((currentTime - entry.getValue()) > maxAgeMillis) {
                emitters.remove(entry.getKey()); // emitters에서도 함께 삭제
                return true;
            }
            return false;
        });
    }

}
