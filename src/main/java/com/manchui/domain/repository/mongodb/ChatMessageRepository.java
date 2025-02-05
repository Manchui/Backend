package com.manchui.domain.repository.mongodb;

import com.manchui.domain.entity.mongodb.ChatMessage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessage, String> {

    Flux<ChatMessage> findByRoomIdAndCreatedAtGreaterThanEqualOrderByCreatedAtDesc(String roomId, LocalDateTime createdAt);

    @Query(value = "{ 'roomId': ?0, '_id': { $lt: ?1 }, 'createdAt': { $lt: ?2 } }", sort = "{ '_id': -1 }")
    Flux<ChatMessage> findByRoomIdAndIdLessThanAndCreatedAtGreaterThanEqualOrderByIdDesc(String roomId, ObjectId lastMessageId, LocalDateTime createdAt);

    Mono<ChatMessage> findFirstByRoomIdOrderByCreatedAtDesc(String roomId);
}
