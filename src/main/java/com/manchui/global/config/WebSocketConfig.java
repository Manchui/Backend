package com.manchui.global.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

    @Value("${message-broker.relay-host}")
    private String relayHost;

    @Value("${message-broker.virtual-host}")
    private String virtualHost;

    @Value("${message-broker.relay-port}")
    private int relayPort;

    @Value("${message-broker.system-login}")
    private String systemLogin;

    @Value("${message-broker.system-passcode}")
    private String systemPasscode;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {

        // 메시지 브로커 설정
        // url을 chat/room/3 -> chat.room.3으로 참조하기 위한 설정
        config.setPathMatcher(new AntPathMatcher("."));

        // 클라이언트에서 메시지를 전송할 경로의 prefix 설정
        // STOMP 브로커를 RabbitMQ에 연결하기 위한 Relay 설정
        config.setApplicationDestinationPrefixes("/pub")
                .enableStompBrokerRelay("/exchange")
                .setRelayHost(relayHost)
                .setVirtualHost(virtualHost)
                .setRelayPort(relayPort)
                .setSystemLogin(systemLogin)
                .setSystemPasscode(systemPasscode)
                .setClientLogin(systemLogin)
                .setClientPasscode(systemPasscode);
    }

    // 클라이언트의 STOMP WebSocket 연결을 위한 엔드포인트 설정
    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/ws") // WebSocket 연결 엔드포인트 설정
                .setAllowedOriginPatterns("*");
    }
}
