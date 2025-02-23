package com.manchui.domain.gathering.service;

import com.manchui.domain.gathering.dto.GatheringCreateRequest;
import com.manchui.domain.chat.entity.mysql.ChatRoom;
import com.manchui.domain.gathering.entity.Gathering;
import com.manchui.domain.user.entity.User;

import java.time.LocalDateTime;

public interface GatheringStore {

    Gathering saveGathering(GatheringCreateRequest createRequest, User user, LocalDateTime gatheringDate, LocalDateTime dueDate, ChatRoom chatRoom);

    void saveAttendance(User user, Gathering gathering);

}
