package com.manchui.domain.dto.chat;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;

@Getter
@AllArgsConstructor
public class ChatMessageSliceResponse {

    private List<ChatMessageResponse> chatMessageResponseList;
    private Boolean hasNext;
    private String nextCursor;
}
