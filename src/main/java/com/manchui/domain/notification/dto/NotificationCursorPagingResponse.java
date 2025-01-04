package com.manchui.domain.notification.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class NotificationCursorPagingResponse {

    private int notificationCount;

    private List<NotificationResponse> notificationContent;

    private Long nextCursor;

}
