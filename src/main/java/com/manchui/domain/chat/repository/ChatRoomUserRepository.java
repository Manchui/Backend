package com.manchui.domain.chat.repository;


import com.manchui.domain.chat.entity.mysql.ChatRoom;
import com.manchui.domain.chat.entity.mysql.ChatRoomUser;
import com.manchui.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ChatRoomUserRepository extends JpaRepository<ChatRoomUser, Long> {

    List<ChatRoomUser> findByChatRoomEqualsAndDeletedAtIsNull(ChatRoom chatRoom);

    Optional<ChatRoomUser> findByUserEqualsAndChatRoomEqualsAndDeletedAtIsNull(User user, ChatRoom chatRoom);

    Optional<ChatRoomUser> findByUserEqualsAndChatRoomEquals(User user, ChatRoom chatRoom);

    List<ChatRoomUser> findByUserAndDeletedAtIsNull(User user);
}
