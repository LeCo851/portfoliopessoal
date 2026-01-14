package com.leandrocoelho.portfoliopessoal.controller;


import com.leandrocoelho.portfoliopessoal.model.dto.ChatRequestDTO;
import com.leandrocoelho.portfoliopessoal.model.dto.ChatResponseDTO;
import com.leandrocoelho.portfoliopessoal.service.ChatService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
@Tag(name = "Chatbot com IA generativa", description = "Endpoint para comunicação com Chatbot")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    @Operation(summary = "Conversar com chatbot", description = "Envia o request para o grok baseado somente nos dados internos e recebe uma resposta")
    public ResponseEntity<ChatResponseDTO> chat(@RequestBody ChatRequestDTO chatRequestDTO){
        if(chatRequestDTO.question() == null || chatRequestDTO.question().trim().isEmpty()){
            return ResponseEntity.badRequest().body(new ChatResponseDTO("Por favor, faça uma pergunta"));
        }
        try{
            String answer = chatService.generateResponse(chatRequestDTO.question());
            return ResponseEntity.ok(new ChatResponseDTO(answer));
        }catch (Exception e){
            return ResponseEntity.status(429).body(new ChatResponseDTO("Limite de token da IA excedido"));
        }
    }
}
