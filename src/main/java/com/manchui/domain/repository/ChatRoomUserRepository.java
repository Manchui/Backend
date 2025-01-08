package com.manchui.domain.repository;


import com.manchui.domain.entity.ChatRoom;
import com.manchui.domain.entity.ChatRoomUser;
import com.manchui.domain.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {

    List<ChatRoomUser> findByChatRoomEquals(ChatRoom chatRoom);

    Optional<ChatRoomUser> findByUserEqualsAndChatRoomEquals(User user, ChatRoom chatRoom);
}
