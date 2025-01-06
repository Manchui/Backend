package com.manchui.domain.repository.mongodb;

import com.manchui.domain.entity.mongodb.ChatMessage;
import org.bson.types.ObjectId;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface ChatMessageRepository extends ReactiveMongoRepository<ChatMessage, String> {

    Flux<ChatMessage> findByRoomIdOrderByCreatedAtDesc(String roomId);

    @Query(value = "{ 'roomId': ?0, '_id': { $lt: ?1 } }", sort = "{ '_id': -1 }")
    Flux<ChatMessage> findByRoomIdAndIdLessThanOrderByIdDesc(String roomId, ObjectId lastMessageId);
}
