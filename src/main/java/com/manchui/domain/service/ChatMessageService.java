package com.manchui.domain.service;

import com.manchui.domain.dto.chat.ChatMessageRequest;
import com.manchui.domain.dto.chat.ChatMessageResponse;
import com.manchui.domain.entity.mongodb.ChatMessage;
import com.manchui.domain.repository.mongodb.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;

    // 채팅 저장 메서드
    public Mono<ChatMessage> chatMessageSave(ChatMessageRequest chatMessageRequest, String roomId) {

        return chatMessageRepository.save(new ChatMessage(roomId, chatMessageRequest.getSender(),
                chatMessageRequest.getMessage(), LocalDateTime.now()));
    }

    //채팅 목록 조회 메서드
    public Mono<List<ChatMessageResponse>> findChatList(String roomId) {

        // 채팅방 메시지 목록 조회
        Flux<ChatMessage> chatMessages = chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(roomId);

        // 반환 값에 맞게 변환
        return chatMessages.map(chatMessage -> new ChatMessageResponse(
                chatMessage.getSender(),
                chatMessage.getMessage(),
                chatMessage.getCreatedAt()
        )).collectList();
    }
}
