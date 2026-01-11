package com.leandrocoelho.portfoliopessoal.controller;


import com.leandrocoelho.portfoliopessoal.model.dto.ChatRequestDTO;
import com.leandrocoelho.portfoliopessoal.model.dto.ChatResponseDTO;
import com.leandrocoelho.portfoliopessoal.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
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
