package com.manchui.domain.controller;

import com.manchui.domain.chat.entity.mongodb.ChatMessage;
import com.manchui.domain.chat.repository.mongodb.ChatMessageRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@RestController
@RequiredArgsConstructor
@Slf4j
public class TestController {

    private final ChatMessageRepository chatRepository;

    @GetMapping("/test")
    public Flux<ChatMessage> test() {
        return chatRepository.findAll();
    }
}
