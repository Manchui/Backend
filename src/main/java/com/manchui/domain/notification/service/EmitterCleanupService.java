package com.manchui.domain.notification.service;

import com.manchui.domain.notification.repository.EmitterRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EmitterCleanupService {

    private final EmitterRepository emitterRepository;

    /**
     * 매 5분마다 실행하여 10분 이상 된 Emitter를 삭제.
     */
    @Scheduled(fixedRate = 300_000) // 5분 간격 실행
    public void cleanupOldEmitters() {

        long maxAgeMillis = 10 * 60 * 1000; // 10분
        emitterRepository.removeOldEmitters(maxAgeMillis);
        System.out.println("오래된 Emitter 정리 완료.");
    }

}
