package com.manchui.domain.gathering.repository;

import com.manchui.domain.chat.entity.mysql.ChatRoom;
import com.manchui.domain.gathering.entity.Gathering;
import com.manchui.domain.user.entity.User;
import com.manchui.domain.gathering.repository.querydsl.GatheringCursorQueryDsl;
import com.manchui.domain.gathering.repository.querydsl.GatheringQueryDsl;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface GatheringRepository extends JpaRepository<Gathering, Long>, GatheringQueryDsl, GatheringCursorQueryDsl {

    Optional<Gathering> findByUserAndGroupName(User user, String groupName);

    Page<Gathering> findByUserEquals(User user, Pageable pageable);

    Page<Gathering> findByIdIn(List<Long> gatheringIdList, Pageable pageable);

    List<Gathering> findByUserAndIsClosedAndIsCanceled(User user, boolean isClosed, boolean isCanceled);

    Optional<Gathering> findByChatRoomEquals(ChatRoom chatRoom);
}
