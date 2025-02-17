package com.manchui.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ChatRoomListDetail {

    private String roomId;
    private String image;
    private String chatRoomTitle;
    private int userNum;
    private LocalDateTime lastMessageTime;
    private String lastMessage;
    private String lastMessageUserName;
}
