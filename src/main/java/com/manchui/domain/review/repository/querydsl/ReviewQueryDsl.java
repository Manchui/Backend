package com.manchui.domain.review.repository.querydsl;

import com.manchui.domain.review.dto.ReviewDetailInfo;
import com.manchui.domain.review.dto.ReviewInfo;
import com.manchui.domain.review.dto.ReviewScoreInfo;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ReviewQueryDsl {

    ReviewScoreInfo getScoreStatisticsByGathering(Long gatheringId);

    ReviewScoreInfo getScoreStatistics(String query, String location, String category, String startDate, String endDate, Integer score);

    Page<ReviewInfo> getReviewInfoList(Pageable pageable, Long gatheringId);

    Page<ReviewDetailInfo> getReviewDetailInfo(Pageable pageable, String query, String location, String startDate, String endDate, String category, String sort, Integer score);

    long getScoreReviewCount(String query, String location, String startDate, String endDate, String category, int score);

    long getScoreReviewCountWithoutFilter();

    ReviewScoreInfo getScoreStatisticsWithoutFilter();

}
