package com.bms.controller;

import com.bms.service.ChatbotService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/chatbot")
@RequiredArgsConstructor
public class ChatbotController {

    private final ChatbotService chatbotService;

    @PostMapping("/ask")
    public ResponseEntity<?> askChatbot(@RequestBody Map<String, String> request) {
        String query = request.getOrDefault("query", "");
        if (query.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("response", "Please ask a valid question."));
        }
        // Security: Limit query length to prevent memory abuse
        if (query.length() > 500) {
            return ResponseEntity.badRequest().body(Map.of("response", "Query too long. Maximum 500 characters allowed."));
        }
        
        String response = chatbotService.getResponse(query);
        return ResponseEntity.ok(Map.of("response", response));
    }
}
