package com.manchui.domain.attendance.repository;

import com.manchui.domain.attendance.entity.Attendance;
import com.manchui.domain.gathering.entity.Gathering;
import com.manchui.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {

    Optional<Attendance> findByUserAndGathering(User user, Gathering gathering);

    List<Attendance> findByGathering(Gathering gathering);

    int countByGatheringAndDeletedAtIsNull(Gathering gathering);

    List<Attendance> findByUserAndDeletedAtIsNull(User user);

}
