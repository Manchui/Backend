package com.manchui.domain.review.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewScoreResponse {

    private long scoreReviewCount;

    private ReviewScoreInfo scoreList;

}
