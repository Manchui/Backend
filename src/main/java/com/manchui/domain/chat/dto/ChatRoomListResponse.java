package com.manchui.domain.chat.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import java.util.List;

@Getter
@AllArgsConstructor
public class ChatRoomListResponse {

    List<ChatRoomListDetail> cHatRoomListDetailList;
}

