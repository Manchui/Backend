package com.manchui.global.handler;

import com.manchui.domain.auth.dto.CustomUserDetails;
import com.manchui.domain.user.entity.User;
import com.manchui.domain.user.repository.UserRepository;
import com.manchui.domain.auth.service.RedisRefreshTokenService;
import com.manchui.global.exception.CustomException;
import com.manchui.global.exception.ErrorCode;
import com.manchui.global.jwt.JWTUtil;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.security.SignatureException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.stomp.StompCommand;
import org.springframework.messaging.simp.stomp.StompHeaderAccessor;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.messaging.support.MessageHeaderAccessor;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(Ordered.HIGHEST_PRECEDENCE + 99)
@Slf4j
public class StompHandler implements ChannelInterceptor {

    private final JWTUtil jwtUtil;
    private final RedisRefreshTokenService redisRefreshTokenService;
    private final UserRepository userRepository;


    // STOMP 메시지 전송 전에 가로채서 처리할 로직
    @Override
    public Message<?> preSend(Message<?> message, MessageChannel channel) {
        // STOMP 헤더를 핸들링하기 위한 액세서 생성
        StompHeaderAccessor accessor = MessageHeaderAccessor.getAccessor(message, StompHeaderAccessor.class);

        // 만약 CONNECT 커맨드라면, JWT 검증 수행
        if(StompCommand.CONNECT.equals(accessor.getCommand())){
            String authorizationHeader = String.valueOf(accessor.getFirstNativeHeader("Authorization"));
            validateAccessToken(authorizationHeader);

            // 인증 정보(Authentication)를 SecurityContext에서 가져와 STOMP 메시지의 사용자(User)로 설정
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null) {
                accessor.setUser(auth); // STOMP 메시지의 Principal에 사용자 정보를 저장
            }
        }

        // 정상 처리된 경우 메시지를 그대로 리턴
        return message;
    }

    private void validateAccessToken(String authorization) {
        if (authorization == null || !authorization.startsWith("Bearer ")) {

            throw new CustomException(ErrorCode.MISSING_AUTHORIZATION_ACCESS_TOKEN);
        }

        //Bearer 부분 제거 후 순수 토큰만 획득
        String accessToken = authorization.split(" ")[1];

        //응답 header에 accessToken이 없는 경우
        if (accessToken == null) {

            throw new CustomException(ErrorCode.MISSING_AUTHORIZATION_ACCESS_TOKEN);
        }

        //accessToken이 만료된 경우
        try {
            jwtUtil.isExpired(accessToken);
        } catch (ExpiredJwtException e) {
            throw new CustomException(ErrorCode.EXPIRED_JWT);
        } catch (SignatureException e) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        String category = jwtUtil.getCategory(accessToken);

        if (!category.equals("access")) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        String userEmail = jwtUtil.getUsername(accessToken);
        //Redis에 저장된 access 토큰 확인
        if (!redisRefreshTokenService.existsByAccessToken(userEmail)) {
            throw new CustomException(ErrorCode.INVALID_ACCESS_TOKEN);
        }

        User user = userRepository.findByEmail(userEmail);
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        // Authentication 객체 생성
        Authentication authentication = new UsernamePasswordAuthenticationToken(customUserDetails, null, customUserDetails.getAuthorities());

        // SecurityContext 에 등록
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }
}
