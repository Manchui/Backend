package com.manchui.domain.service;

import com.manchui.domain.dto.CustomUserDetails;
import com.manchui.domain.dto.chat.ChatMessageRequest;
import com.manchui.domain.dto.chat.ChatMessageResponse;
import com.manchui.domain.dto.chat.ChatMessageSliceResponse;
import com.manchui.domain.entity.mongodb.ChatMessageType;
import com.manchui.domain.entity.ChatRoom;
import com.manchui.domain.entity.ChatRoomUser;
import com.manchui.domain.entity.User;
import com.manchui.domain.entity.mongodb.ChatMessage;
import com.manchui.domain.repository.ChatRoomRepository;
import com.manchui.domain.repository.ChatRoomUserRepository;
import com.manchui.domain.repository.UserRepository;
import com.manchui.domain.repository.mongodb.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bson.types.ObjectId;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ChatMessageService {

    private final ChatMessageRepository chatMessageRepository;
    private final UserRepository userRepository;
    private final ChatRoomRepository chatRoomRepository;
    private final ChatRoomUserRepository chatRoomUserRepository;

    // 채팅 저장 메서드
    public Mono<ChatMessage> chatMessageSave(ChatMessageRequest chatMessageRequest, String roomId) {

        return chatMessageRepository.save(new ChatMessage(roomId, ChatMessageType.MESSAGE,chatMessageRequest.getSender(),
                chatMessageRequest.getMessage(), LocalDateTime.now()));
    }

    public Mono<ChatMessage> chatQuiteMessageSave(ChatMessageRequest chatMessageRequest, String roomId){

        return chatMessageRepository.save(new ChatMessage(roomId, ChatMessageType.QUITE,chatMessageRequest.getSender(),
                chatMessageRequest.getSender() + chatMessageRequest.getMessage(), LocalDateTime.now()));
    }

    public Mono<ChatMessage> chatRoomOpenMessageSave(ChatMessageRequest chatMessageRequest, String roomId){
        return chatMessageRepository.save(new ChatMessage(roomId, ChatMessageType.OPEN,chatMessageRequest.getSender(),
                chatMessageRequest.getMessage(), LocalDateTime.now()));
    }

    //채팅방 입장 및 채팅 목록 조회 메서드
    public Mono<ChatMessageSliceResponse> findChatList(CustomUserDetails customUserDetails, String roomId, ObjectId lastMessageId, int limit,
                                                        RabbitTemplate rabbitTemplate) {
        // Blocking(JPA) 작업을 다른 스레드풀에서 수행하기 위해 fromCallable 사용
        return Mono.fromCallable(() -> {

                    // 블로킹(JPA) 코드 실행 영역.
                    // 이벤트 루프가 아닌 별도 쓰레드에서 처리할 예정이므로,
                    // 여기서 JPA 호출을 수행해도 WebFlux 이벤트 루프를 방해하지 않는다.

                    // 유저 정보와 채팅방 정보를 블로킹 방식으로 조회
                    String userEmail = customUserDetails.getUsername();
                    User user = userRepository.findByEmail(userEmail);
                    ChatRoom chatRoom = chatRoomRepository.findByRoomId(roomId);

                    // 해당 유저가 채팅방 참여자가 맞는지 확인 후, 없으면 새로 저장 및 입장 메시지
                    Optional<ChatRoomUser> chatRoomUser = chatRoomUserRepository.findByUserEqualsAndChatRoomEquals(user, chatRoom);
                    if (chatRoomUser.isEmpty()) {
                        chatRoomUserRepository.save(new ChatRoomUser(user, chatRoom));
                        chatMessageRepository.save(new ChatMessage(roomId, ChatMessageType.ENTER, user.getName(), user.getName() + " 님이 입장 하셨습니다.", LocalDateTime.now())).block();
                        rabbitTemplate.convertAndSend("chat.exchange", "room." + roomId, new ChatMessageResponse(
                                user.getName(), user.getName() + " 님이 입장 하셨습니다.", ChatMessageType.MESSAGE,LocalDateTime.now()));
                    }

                    // 블로킹 영역에서 최종적으로 roomId만 반환
                    // (이후 flatMapMany로 ReactiveMongoRepository를 호출하기 위해서)
                    return roomId;
                })
                // subscribeOn: 위의 fromCallable 블록을 별도의 쓰레드 풀(boundedElastic)에서 실행
                .subscribeOn(Schedulers.boundedElastic())
                // flatMapMany로 넘겨 받은 roomId로 리액티브 MongoDB 쿼리 수행
                .flatMapMany(rId -> {
                    // lastMessageId == null이면 최신 메시지 조회
                    if (lastMessageId == null) {
                        // 초기 요청 (최신 메시지 limit + 1개)
                        return chatMessageRepository.findByRoomIdOrderByCreatedAtDesc(rId)
                                .take(limit + 1); // // limit+1개 가져와서 다음 페이지 여부 확인
                    } else {
                        // 이후 요청 (lastMessageId 기준)
                        return chatMessageRepository.findByRoomIdAndIdLessThanOrderByIdDesc(rId, lastMessageId)
                                .take(limit + 1);
                    }
                })
                .collectList()
                // 응답 DTO로 변환
                .map(messages -> {
                    // limit + 1개 중 실제 limit개만 표시하고, 나머지 1개로 hasNext 판단
                    boolean hasNext = messages.size() > limit;
                    String nextCursor = null;
                    if(hasNext){
                        nextCursor = messages.get(limit - 1).get_id();
                    }
                    List<ChatMessageResponse> content = messages.stream().limit(limit)
                            .map(chatMessage ->
                                    new ChatMessageResponse(
                                            chatMessage.get_id(),
                                            chatMessage.getSender(),
                                            chatMessage.getChatMessageType(),
                                            chatMessage.getMessage(),
                                            chatMessage.getCreatedAt()
                                    )).collect(Collectors.toList());

                    return new ChatMessageSliceResponse(content, hasNext, nextCursor);
                });
    }
}
