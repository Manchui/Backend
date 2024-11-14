package com.manchui.domain.dto.review;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ReviewScoreResponse {

    private long scoreReviewCount;

    private ReviewScoreInfo scoreList;

}
