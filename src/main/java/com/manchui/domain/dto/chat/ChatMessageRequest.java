package com.manchui.domain.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ChatMessageRequest {

    private String sender;
    private String message;
}
