package com.manchui.domain.chat.dto;

import com.manchui.domain.chat.entity.mongodb.ChatMessageType;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessageResponse {

    private String _id;
    private String sender;
    private ChatMessageType chatMessageType;
    private String message;
    private LocalDateTime createdAt;

    public ChatMessageResponse(String sender, String message, ChatMessageType chatMessageType, LocalDateTime createdAt) {
        this.sender = sender;
        this.message = message;
        this.chatMessageType = chatMessageType;
        this.createdAt = createdAt;
    }
}
