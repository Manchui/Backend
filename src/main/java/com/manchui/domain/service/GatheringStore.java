package com.manchui.domain.service;

import com.manchui.domain.dto.gathering.GatheringCreateRequest;
import com.manchui.domain.entity.ChatRoom;
import com.manchui.domain.entity.Gathering;
import com.manchui.domain.entity.User;

import java.time.LocalDateTime;

public interface GatheringStore {

    Gathering saveGathering(GatheringCreateRequest createRequest, User user, LocalDateTime gatheringDate, LocalDateTime dueDate, ChatRoom chatRoom);

    void saveAttendance(User user, Gathering gathering);

}
