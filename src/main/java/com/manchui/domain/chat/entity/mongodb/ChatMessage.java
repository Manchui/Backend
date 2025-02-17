package com.manchui.domain.chat.entity.mongodb;

import jakarta.persistence.Id;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "ChatMessage")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChatMessage {

    @Id
    private String _id;
    private String roomId;
    private ChatMessageType chatMessageType;
    private String sender;
    private String message;
    private LocalDateTime createdAt;

    public ChatMessage(String roomId, ChatMessageType chatMessageType, String sender, String message, LocalDateTime createdAt) {
        this.roomId = roomId;
        this.chatMessageType = chatMessageType;
        this.sender = sender;
        this.message = message;
        this.createdAt = createdAt;
    }
}
