package com.manchui.global.handler;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.manchui.global.exception.CustomException;
import com.manchui.global.exception.ErrorCode;
import com.manchui.global.response.ErrorResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.socket.messaging.StompSubProtocolErrorHandler;

import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
@Slf4j
public class StompErrorHandler extends StompSubProtocolErrorHandler {

    private final ObjectMapper objectMapper;
    @Override
    public Message<byte[]> handleClientMessageProcessingError(Message<byte[]> clientMessage, Throwable ex) {

        StompHeaderAccessor accessor = StompHeaderAccessor.create(StompCommand.ERROR);
        accessor.setLeaveMutable(true); // 헤더를 편집 가능하도록 설정

        // 커스텀 예외라면, 에러코드/메시지를 헤더에 담기
        if (ex.getCause() instanceof CustomException) {
            CustomException customException = (CustomException) ex.getCause();
            ErrorCode errorCode = customException.getErrorCode();

            accessor.addNativeHeader("errorCode", String.valueOf(errorCode.getHttpStatus().value()));
            accessor.addNativeHeader("errorMessage", errorCode.getMessage());

            // 에러 객체 생성
            ErrorResponse errorResponse = ErrorResponse.create()
                    .httpStatus(errorCode.getHttpStatus())
                    .message(errorCode.getMessage());

            // JSON 변환
            String payload = null;
            try {
                payload = objectMapper.writeValueAsString(errorResponse);
            } catch (JsonProcessingException e) {
                log.error("JSON 직렬화 오류 = {}", e.getMessage(), e);
                payload = "{\"error\":\"JSON serialization error\"}";
            }

            // ERROR 프레임 바디에 JSON 삽입 후 반환
            return MessageBuilder.createMessage(payload.getBytes(StandardCharsets.UTF_8), accessor.getMessageHeaders());
        }else{

            // CustomException이 아닌 경우, 기본 처리 로직 사용
            return super.handleClientMessageProcessingError(clientMessage, ex);
        }
    }
}
