package com.manchui.domain.service;

import com.manchui.domain.dto.UserInfo;
import com.manchui.domain.dto.chat.ChatRoomUserListResponse;
import com.manchui.domain.entity.ChatRoom;
import com.manchui.domain.entity.ChatRoomUser;
import com.manchui.domain.repository.ChatRoomRepository;
import com.manchui.domain.repository.ChatRoomUserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatRoomService {

    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatRoomRepository chatRoomRepository;

    // 채팅방 회원 목록 조회
    public ChatRoomUserListResponse chatRoomUserList(String roomId) {

        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);

        List<ChatRoomUser> userList = chatRoomUserRepository.findByChatRoomEquals(chatRoom);

        List<UserInfo> userInfoList = userList.stream().map(m -> new UserInfo(
                m.getUser().getName(),
                m.getUser().getProfileImagePath()
        )).collect(Collectors.toList());

        return new ChatRoomUserListResponse(userInfoList);
    }
}
