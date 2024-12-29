package com.manchui.domain.repository.mongodb;

import com.manchui.domain.entity.mongodb.ChatMessage;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessage, String> {

    Flux<ChatMessage> findByRoomIdOrderByCreatedAtDesc(String roomId);
}
