package com.manchui.domain.notification.controller;

import com.manchui.domain.auth.dto.CustomUserDetails;
import com.manchui.domain.notification.dto.NotificationCursorPagingResponse;
import com.manchui.domain.notification.service.NotificationService;
import com.manchui.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

@Slf4j
@RestController
@RequestMapping("/api/notifications")
@Tag(name = "Notification", description = "알림 API")
@RequiredArgsConstructor
public class NotificationController {

    private final NotificationService notificationService;

    @Operation(summary = "알림 목록 조회", description = "특정 사용자의 알림 목록을 가져옵니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 목록 조회 성공"),
            @ApiResponse(responseCode = "401", description = "인증 실패"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @GetMapping
    public ResponseEntity<SuccessResponse<NotificationCursorPagingResponse>> getNotifications(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestParam(value = "unreadOnly", required = false, defaultValue = "false") boolean unreadOnly,
            @RequestParam(required = false) Long cursor,
            @RequestParam(required = false, defaultValue = "10") int size) {

        NotificationCursorPagingResponse notifications =
                notificationService.getNotifications(userDetails.getUsername(), unreadOnly, cursor, size);

        return ResponseEntity.ok(SuccessResponse.successWithData(notifications));
    }

    @Operation(summary = "알림 읽음 처리", description = "특정 알림을 읽음 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 읽음 처리 성공"),
            @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PatchMapping("/{notificationId}/read")
    public ResponseEntity<SuccessResponse<String>> markNotificationAsRead(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        notificationService.markAsRead(notificationId, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.successWithData("알림이 읽음 상태로 변경되었습니다."));
    }

    @Operation(summary = "알림 일괄 읽음 처리", description = "사용자의 모든 알림을 읽음 처리합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 일괄 읽음 처리 성공"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @PatchMapping("/read-all")
    public ResponseEntity<SuccessResponse<String>> markAllNotificationsAsRead(@AuthenticationPrincipal CustomUserDetails userDetails) {

        notificationService.markAllAsRead(userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.successWithData("모든 알림이 읽음 상태로 변경되었습니다."));
    }

    @Operation(summary = "알림 삭제", description = "특정 알림을 삭제합니다.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "알림 삭제 성공"),
            @ApiResponse(responseCode = "404", description = "알림을 찾을 수 없음"),
            @ApiResponse(responseCode = "500", description = "서버 오류")
    })
    @DeleteMapping("/{notificationId}")
    public ResponseEntity<SuccessResponse<String>> deleteNotification(
            @PathVariable Long notificationId,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        notificationService.deleteNotification(notificationId, userDetails.getUsername());
        return ResponseEntity.ok(SuccessResponse.successWithData("알림이 삭제되었습니다."));
    }

    @Tag(name = "SSE", description = "SSE 연결 API")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "SSE 연결 성공"),
            @ApiResponse(responseCode = "500", description = "SSE 연결 실패")
    })
    @Operation(summary = "SSE 연결")
    @GetMapping(value = "/subscribe", produces = "text/event-stream")
    public SseEmitter subscribe(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @RequestHeader(value = "Last-Event-ID", required = false, defaultValue = "") String lastEventId) {

        log.info("SSE 연결 요청 수신 - email: {}, Last-Event-ID: {}", userDetails.getUsername(), lastEventId);
        return notificationService.subscribe(userDetails.getUsername(), lastEventId);
    }

}
