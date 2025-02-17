package com.manchui.domain.chat.controller;

import com.manchui.domain.chat.dto.*;
import com.manchui.domain.auth.dto.CustomUserDetails;
import com.manchui.domain.chat.service.ChatMessageService;
import com.manchui.domain.chat.service.ChatRoomService;
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
import reactor.core.scheduler.Schedulers;

import java.security.Principal;
import java.time.LocalDateTime;

@RestController
@RequiredArgsConstructor
@Slf4j
public class ChatController {

    private final ChatMessageService chatMessageService;
    private final RabbitTemplate rabbitTemplate;
    private final ChatRoomService chatRoomService;

    @GetMapping("/api/chat/list/{roomId}")
    public Mono<ResponseEntity<SuccessResponse<ChatMessageSliceResponse>>> chatList(@AuthenticationPrincipal CustomUserDetails customUserDetails,
            @PathVariable String roomId,
            @RequestParam(required = false) ObjectId lastMessageId,
            @RequestParam(required = false, defaultValue = "20") int limit) {

        Mono<ChatMessageSliceResponse> response = chatMessageService.findChatList(customUserDetails, roomId, lastMessageId, limit, rabbitTemplate);

        return response.map(SuccessResponse::successWithData)
                .map(ResponseEntity::ok);
    }

    @MessageMapping("chat.room.{roomId}")
    public Mono<ResponseEntity<SuccessResponse<Void>>> chat(@DestinationVariable String roomId, @RequestBody ChatMessageRequest chatMessageRequest) {

        // 채팅 메시지를 데이터베이스에 저장하고, 저장이 완료되면 RabbitMQ를 통해 해당 채팅방(roomId)으로 메시지를 전송
        return chatMessageService.chatMessageSave(chatMessageRequest, roomId).doOnSuccess(chatMessage -> {
            rabbitTemplate.convertAndSend("chat.exchange", "room." + roomId, new ChatMessageResponse(
                    chatMessageRequest.getSender(), chatMessageRequest.getMessage(), chatMessage.getChatMessageType(), LocalDateTime.now()));
            // 모든 작업이 성공적으로 완료되면 클라이언트에 성공 응답 반환
        }).then(Mono.just(ResponseEntity.ok().body(SuccessResponse.successWithNoData("메시지 전송 성공"))));
    }

    @MessageMapping("chat.leave.{roomId}")
    public Mono<ResponseEntity<SuccessResponse<Void>>> chatRoomLeave(@DestinationVariable String roomId,
                                                                     @RequestBody ChatMessageRequest chatMessageRequest,
                                                                     Principal principal){

        return chatMessageService.chatQuiteMessageSave(chatMessageRequest, roomId).flatMap(chatMessage -> {
           // 채팅방 나가기 메시지 전송
            rabbitTemplate.convertAndSend("chat.exchange", "room." + roomId, new ChatMessageResponse(
                    chatMessageRequest.getSender(), chatMessageRequest.getSender() + chatMessageRequest.getMessage(), chatMessage.getChatMessageType(), LocalDateTime.now()));

           // 블로킹 JPA 메서드는 별도 스레드에서 실행
           // 채팅방 유저 목록에서 유저 softDelete
           return Mono.fromCallable(() -> {
               chatRoomService.chatRoomQuite(principal.getName(), roomId);
               return null;
           }).subscribeOn(Schedulers.boundedElastic());
        }).then(Mono.just(ResponseEntity.ok().body(SuccessResponse.successWithNoData("채팅방 나기기 성공"))));
    }

    @GetMapping("/api/chat/user/list/{roomId}")
    public ResponseEntity<SuccessResponse<ChatRoomUserListResponse>> chatRoomUserList(@PathVariable String roomId) {

        ChatRoomUserListResponse response = chatRoomService.chatRoomUserList(roomId);

        return ResponseEntity.ok(SuccessResponse.successWithData(response));
    }

    @GetMapping("/api/chat/room/list")
    public ResponseEntity<SuccessResponse<ChatRoomListResponse>> chatRoomList(@AuthenticationPrincipal CustomUserDetails customUserDetails) {

        return ResponseEntity.ok().body(SuccessResponse.successWithData(chatRoomService.chatRoomList(customUserDetails)));
    }
}