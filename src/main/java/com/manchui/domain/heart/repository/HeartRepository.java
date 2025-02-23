package com.manchui.domain.heart.repository;

import com.manchui.domain.gathering.entity.Gathering;
import com.manchui.domain.heart.entity.Heart;
import com.manchui.domain.user.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface HeartRepository extends JpaRepository<Heart, Long> {

    List<Heart> findByGathering(Gathering gathering);

    Optional<Heart> findByUserAndGathering(User user, Gathering gathering);

}
