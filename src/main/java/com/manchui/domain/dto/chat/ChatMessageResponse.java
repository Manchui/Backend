package com.manchui.domain.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatMessageResponse {

    private String sender;
    private String message;
    private LocalDateTime createdAt;
}
