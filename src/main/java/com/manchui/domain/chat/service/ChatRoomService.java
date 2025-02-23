package com.manchui.domain.chat.service;

import com.manchui.domain.chat.entity.mysql.ChatRoom;
import com.manchui.domain.chat.entity.mysql.ChatRoomUser;
import com.manchui.domain.chat.repository.ChatRoomRepository;
import com.manchui.domain.chat.repository.ChatRoomUserRepository;
import com.manchui.domain.auth.dto.CustomUserDetails;
import com.manchui.domain.gathering.entity.Gathering;
import com.manchui.domain.gathering.repository.GatheringRepository;
import com.manchui.domain.image.entity.Image;
import com.manchui.domain.image.repository.ImageRepository;
import com.manchui.domain.user.dto.UserInfo;
import com.manchui.domain.chat.dto.ChatRoomListDetail;
import com.manchui.domain.chat.dto.ChatRoomListResponse;
import com.manchui.domain.chat.dto.ChatRoomUserListResponse;
import com.manchui.domain.chat.entity.mongodb.ChatMessage;
import com.manchui.domain.chat.repository.mongodb.ChatMessageRepository;
import com.manchui.domain.user.entity.User;
import com.manchui.domain.user.repository.UserRepository;
import com.manchui.global.exception.CustomException;
import com.manchui.global.exception.ErrorCode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatRoomService {

    private final ChatRoomUserRepository chatRoomUserRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final UserRepository userRepository;
    private final GatheringRepository gatheringRepository;
    private final ImageRepository imageRepository;
    private final ChatMessageRepository chatMessageRepository;

    // 채팅방 회원 목록 조회
    public ChatRoomUserListResponse chatRoomUserList(String roomId) {

        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);

        List<ChatRoomUser> userList = chatRoomUserRepository.findByChatRoomEqualsAndDeletedAtIsNull(chatRoom);

        List<UserInfo> userInfoList = userList.stream().map(m -> new UserInfo(
                m.getUser().getName(),
                m.getUser().getProfileImagePath()
        )).collect(Collectors.toList());

        return new ChatRoomUserListResponse(userInfoList);
    }

    // 사용자가 속한 채팅방 목록 조회
    public ChatRoomListResponse chatRoomList(CustomUserDetails customUserDetails) {
        // 회원 조회
        String userEmail = customUserDetails.getUsername();
        User user = userRepository.findByEmail(userEmail);
        // 사용자가 속하 ChatRoomUser 조회
        List<ChatRoomUser> chatRoomUsers = chatRoomUserRepository.findByUserAndDeletedAtIsNull(user);
        // ChatRoomUser -> DTO(ChatRoomListDetail) 변환
        List<ChatRoomListDetail> chatRoomListDetails = chatRoomUsers.stream().map((m -> {
            ChatRoom chatRoom = m.getChatRoom();
            Gathering gathering = gatheringRepository.findByChatRoomEquals(chatRoom).orElseThrow(
                    () -> new CustomException(ErrorCode.GATHERING_NOT_FOUND));

            Image image = imageRepository.findByGatheringId(gathering.getId());
            List<ChatRoomUser> chatRoomEquals = chatRoomUserRepository.findByChatRoomEqualsAndDeletedAtIsNull(chatRoom);

            ChatMessage lastMessage = chatMessageRepository.findFirstByRoomIdOrderByCreatedAtDesc(chatRoom.getRoomId()).block();
            return new ChatRoomListDetail(m.getChatRoom().getRoomId(), image.getFilePath(), gathering.getGroupName(),
                    chatRoomEquals.size(), lastMessage.getCreatedAt(), lastMessage.getMessage(), lastMessage.getSender());
        })).sorted(Comparator.comparing(ChatRoomListDetail::getLastMessageTime).reversed()).collect(Collectors.toList());

        return new ChatRoomListResponse(chatRoomListDetails);
    }

    // 채팅방에 속한 사용자 softDelete
    @Transactional
    public void chatRoomQuite(String email, String roomId) {

        User user = userRepository.findByEmail(email);
        ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);
        ChatRoomUser chatRoomUser = chatRoomUserRepository.findByUserEqualsAndChatRoomEqualsAndDeletedAtIsNull(user, chatRoom).orElseThrow(
                () -> new CustomException(ErrorCode.MEMBER_NOT_IN_CHATROOM)
        );
        chatRoomUser.softDelete();
    }

}
