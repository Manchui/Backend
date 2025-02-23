package com.manchui.domain.review.repository;

import com.manchui.domain.gathering.entity.Gathering;
import com.manchui.domain.review.entity.Review;
import com.manchui.domain.review.repository.querydsl.ReviewQueryDsl;
import com.manchui.domain.user.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ReviewRepository extends JpaRepository<Review, Long>, ReviewQueryDsl {

    Optional<Review> findByGatheringAndUser(Gathering gathering, User user);

    Page<Review> findByUser(User user, Pageable pageable);

    Optional<Review> findByIdAndDeletedAtIsNull(Long reviewId);

    List<Review> findByUserAndDeletedAtIsNull(User user);

}
