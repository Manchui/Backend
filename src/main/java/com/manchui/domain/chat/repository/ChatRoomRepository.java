package com.manchui.domain.chat.repository;

import com.manchui.domain.chat.entity.mysql.ChatRoom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ChatRoomRepository extends JpaRepository<ChatRoom, Long> {

    ChatRoom findByRoomId(String roomId);
}
