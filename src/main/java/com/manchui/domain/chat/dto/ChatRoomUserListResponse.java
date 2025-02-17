package com.manchui.domain.chat.dto;

import com.manchui.domain.user.dto.UserInfo;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
    public class ChatRoomUserListResponse {

    private List<UserInfo> userInfoList;
}
