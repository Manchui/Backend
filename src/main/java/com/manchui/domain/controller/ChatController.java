package com.manchui.domain.controller;

import com.manchui.domain.dto.CustomUserDetails;
import com.manchui.domain.dto.chat.ChatMessageRequest;
import com.manchui.domain.dto.chat.ChatMessageResponse;
import com.manchui.domain.dto.chat.ChatMessageSliceResponse;
import com.manchui.domain.dto.chat.ChatRoomUserListResponse;
import com.manchui.domain.service.ChatMessageService;
import com.manchui.domain.service.ChatRoomService;
import com.manchui.global.response.SuccessResponse;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final RabbitTemplate rabbitTemplate;
    private final ChatRoomService chatRoomService;

    @GetMapping("/api/chat/list/{roomId}")
    public Mono<ResponseEntity<SuccessResponse<List<ChatMessageResponse>>>> chatList(@PathVariable String roomId) {

        Mono<List<ChatMessageResponse>> response = chatMessageService.findChatList(roomId);

        return response.map(SuccessResponse::successWithData)
                .map(ResponseEntity::ok);
    }

    @MessageMapping("chat.room.{roomId}")
    public Mono<ResponseEntity<SuccessResponse<Void>>> chat(@DestinationVariable String roomId, @RequestBody ChatMessageRequest chatMessageRequest) {

        // 채팅 메시지를 데이터베이스에 저장하고, 저장이 완료되면 RabbitMQ를 통해 해당 채팅방(roomId)으로 메시지를 전송
        return chatMessageService.chatMessageSave(chatMessageRequest, roomId).doOnSuccess(chatMessage -> {
            rabbitTemplate.convertAndSend("chat.exchange", "room." + roomId, new ChatMessageResponse(
                    chatMessageRequest.getSender(), chatMessageRequest.getMessage(), LocalDateTime.now()));
            // 모든 작업이 성공적으로 완료되면 클라이언트에 성공 응답 반환
        }).then(Mono.just(ResponseEntity.ok().body(SuccessResponse.successWithNoData("메시지 전송 성공"))));
    }

    @GetMapping("/api/chat/user/list/{roomId}")
    public ResponseEntity<SuccessResponse<ChatRoomUserListResponse>> chatRoomUserList(@PathVariable String roomId) {

        ChatRoomUserListResponse response = chatRoomService.chatRoomUserList(roomId);

        return ResponseEntity.ok(SuccessResponse.successWithData(response));
    }
}