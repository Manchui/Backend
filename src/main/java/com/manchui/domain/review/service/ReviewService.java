package com.manchui.domain.review.service;

import com.manchui.domain.review.dto.ReviewCreateRequest;
import com.manchui.domain.review.dto.ReviewCreateResponse;
import com.manchui.domain.review.dto.ReviewDetailPagingResponse;
import com.manchui.domain.review.dto.ReviewScoreResponse;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public interface ReviewService {

    ReviewCreateResponse createReview(String email, Long gatheringId, ReviewCreateRequest createResponse);

    ReviewCreateResponse updateReview(String email, Long reviewId, ReviewCreateRequest updateRequest);

    void deleteReview(String email, Long reviewId);

    ReviewDetailPagingResponse searchReview(Pageable pageable, String query, String location, String startDate, String endDate, String category, String sort, Integer score);

    ReviewScoreResponse getReviewScore();

}
