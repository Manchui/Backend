package com.manchui.domain.dto.chat;

import com.manchui.domain.dto.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
    public class ChatRoomUserListResponse {

    private List<UserInfo> userInfoList;
}
