package com.manchui.domain.controller;

import com.manchui.domain.dto.CustomUserDetails;
import com.manchui.domain.dto.review.ReviewCreateRequest;
import com.manchui.domain.dto.review.ReviewCreateResponse;
import com.manchui.domain.dto.review.ReviewDetailPagingResponse;
import com.manchui.domain.dto.review.ReviewScoreResponse;
import com.manchui.domain.service.ReviewService;
import com.manchui.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Tag(name = "Reviews")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
public class ReviewController {

    private final ReviewService reviewService;

    @Operation(summary = "후기 등록", description = "새로운 후기를 등록합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "후기가 등록되었습니다.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "입력값을 확인해주세요."),
            @ApiResponse(responseCode = "404", description = "존재하지 않는 계정입니다.")
    })
    @PostMapping("/{gatheringId}")
    public ResponseEntity<SuccessResponse<ReviewCreateResponse>> createReview(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long gatheringId,
                                                                              @Valid @RequestBody ReviewCreateRequest createRequest) {

        return ResponseEntity.status(201).body(SuccessResponse.successWithData(reviewService.createReview(userDetails.getUsername(), gatheringId, createRequest)));
    }

    @Operation(summary = "후기 수정", description = "후기를 수정합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "후기가 수정되었습니다.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "400", description = "입력값을 확인해주세요."),
            @ApiResponse(responseCode = "403", description = "본인이 작성한 후기만 수정 가능합니다.")
    })
    @PutMapping("/{reviewId}")
    public ResponseEntity<SuccessResponse<ReviewCreateResponse>> updateReview(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long reviewId,
                                                                              @Valid @RequestBody ReviewCreateRequest updateRequest) {

        return ResponseEntity.ok().body(SuccessResponse.successWithData(reviewService.updateReview(userDetails.getUsername(), reviewId, updateRequest)));
    }

    @Operation(summary = "후기 삭제", description = "후기를 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "후기가 삭제되었습니다.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "후기를 찾을 수 없습니다."),
            @ApiResponse(responseCode = "403", description = "본인이 작성한 후기만 삭제 가능합니다.")
    })
    @DeleteMapping("/{reviewId}")
    public ResponseEntity<SuccessResponse<ReviewCreateResponse>> deleteReview(@AuthenticationPrincipal CustomUserDetails userDetails, @PathVariable Long reviewId) {

        reviewService.deleteReview(userDetails.getUsername(), reviewId);
        return ResponseEntity.ok().body(SuccessResponse.successWithNoData("후기가 정상적으로 삭제되었습니다."));
    }

    @Operation(summary = "전체 후기 목록 조회", description = "전체 후기를 조회합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "전체 후기의 목록이 반환되었습니다.",
                    content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "후기를 찾을 수 없습니다.")})
    @GetMapping("")
    public ResponseEntity<SuccessResponse<ReviewDetailPagingResponse>> searchReview(@RequestParam(defaultValue = "1") int page,
                                                                                    @RequestParam int size,
                                                                                    @RequestParam(required = false) String query,
                                                                                    @RequestParam(required = false) String location,
                                                                                    @RequestParam(required = false) String startDate,
                                                                                    @RequestParam(required = false) String endDate,
                                                                                    @RequestParam(required = false) String category,
                                                                                    @RequestParam(required = false) String sort,
                                                                                    @RequestParam(defaultValue = "-1") int score) {

        Pageable pageable = PageRequest.of(page - 1, size, Sort.by("createdAt").descending());
        return ResponseEntity.ok(SuccessResponse.successWithData(reviewService.searchReview(pageable, query, location, startDate, endDate, category, sort, score)));
    }

    @GetMapping("/score")
    public ResponseEntity<SuccessResponse<ReviewScoreResponse>> getReviewScore() {

        return ResponseEntity.ok(SuccessResponse.successWithData(reviewService.getReviewScore()));
    }

}
