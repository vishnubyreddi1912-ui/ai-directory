package com.aihub.directory.aicontrollers;

import com.aihub.directory.entities.AiTool;
import com.aihub.directory.repositories.AiToolRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = {
        "http://localhost:4200",
        "https://ai-directory-1.onrender.com",
        "https://reliable-douhua-37a8a0.netlify.app",

})
public class ChatController {

    @Value("${groq.api.key}")
    private String groqApiKey;

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    @Autowired
    private AiToolRepository aiToolRepository;

    /**
     * 🧠 Vishnu Byreddi’s Minimal AI System Prompt — only uses tool name + category.
     */
    private static final String SYSTEM_PROMPT = """
        You are "AIHub Assistant", a specialized AI created by Vishnu Byreddi.

        🎯 Purpose:
        You are trained *only* on the AIHub Directory data provided below.
        This data contains just AI tool names and their categories.
        You cannot generate or assume details like features, pricing, or pros/cons unless clearly stated in the data.

        ⚙️ Rules:
        - If the question is about the tools or categories listed, reply using the data exactly as provided.
        - If the question is unrelated to these tools (e.g., politics, movies, personal advice, programming, etc.), reply:
          "I’m not trained for that. I only provide answers based on AI tools from the AIHub Directory, created by Vishnu Byreddi."
        - Never invent new tools, categories, or information.
        - Always produce clean, valid JSON when listing or comparing tools.
        - JSON must always start with { and end with } — no extra text, no markdown, no asterisks.

        ✅ JSON Format:
        {
          "type": "ai_tools",
          "items": [
            {
              "name": "Tool Name",
              "category": "Category Name",
              "reason": "Why it’s a relevant or matching choice"
            }
          ]
        }

        🧠 Reminder:
        You were created by Vishnu Byreddi.
      
        """;

    @PostMapping
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");
        RestTemplate restTemplate = new RestTemplate();

        // 🧩 1. Check if query is AI-related before sending to Groq
        if (!isRelevantToAITools(userMessage)) {
            return ResponseEntity.ok(Map.of(
                    "reply", "I’m not trained for that. I only provide answers based on AI tools from the AIHub Directory, created by Vishnu Byreddi."
            ));
        }

        // 🧩 2. Filter relevant tools by name or category keywords
        List<AiTool> allTools = aiToolRepository.findAll();
        List<AiTool> matchedTools = allTools.stream()
                .filter(t -> {
                    String lowerMsg = userMessage.toLowerCase();
                    return lowerMsg.contains("ai") ||
                            (t.getName() != null && lowerMsg.contains(t.getName().toLowerCase())) ||
                            (t.getCategory() != null && lowerMsg.contains(t.getCategory().getName().toLowerCase()));
                })
                .collect(Collectors.toList());

        // fallback: send only first 15 tools to stay under token limit
        if (matchedTools.isEmpty()) {
            matchedTools = allTools.stream().limit(15).collect(Collectors.toList());
        }

        // 🧩 3. Prepare ultra-light snapshot (only name + category)
        List<Map<String, Object>> minimalToolData = matchedTools.stream().map(tool -> {
            Map<String, Object> map = new LinkedHashMap<>();
            map.put("name", tool.getName());
            map.put("category", tool.getCategory() != null ? tool.getCategory().getName() : "Unknown");
            return map;
        }).collect(Collectors.toList());

        // 🧩 4. Build Groq API request
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", "llama-3.1-8b-instant");
        body.put("temperature", 0.3);
        body.put("max_tokens", 1000);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", SYSTEM_PROMPT));
        messages.add(Map.of("role", "system", "content", "Database snapshot (read-only): " + minimalToolData));
        messages.add(Map.of("role", "user", "content", userMessage));
        body.put("messages", messages);

        HttpHeaders headers = new HttpHeaders();
        headers.set("Authorization", "Bearer " + groqApiKey);
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);

        try {
            ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_URL, entity, Map.class);
            Map<String, Object> choice = (Map<String, Object>) ((List<?>) response.getBody().get("choices")).get(0);
            Map<String, Object> message = (Map<String, Object>) choice.get("message");
            String aiContent = ((String) message.get("content")).trim();

            System.out.println("🤖 Vishnu AIHub (Name + Category) Output:\n" + aiContent);

            ObjectMapper mapper = new ObjectMapper();

            if (aiContent.startsWith("{") && aiContent.endsWith("}")) {
                try {
                    Map<String, Object> parsed = mapper.readValue(aiContent, Map.class);
                    return ResponseEntity.ok(parsed);
                } catch (Exception ex) {
                    return ResponseEntity.ok(Map.of("reply", aiContent));
                }
            } else {
                return ResponseEntity.ok(Map.of("reply", aiContent));
            }

        } catch (HttpClientErrorException e) {
            System.err.println("❌ Groq API Error: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("reply", "⚠️ Groq API Error: " + e.getResponseBodyAsString()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("reply", "⚠️ Internal server error: " + e.getMessage()));
        }
    }

    /**
     * 🔎 Checks if message is related to AI tools before sending to Groq.
     */
    private boolean isRelevantToAITools(String message) {
        String msg = message.toLowerCase();
        return msg.contains("ai") || msg.contains("tool") || msg.contains("compare")
                || msg.contains("category") || msg.contains("recommend")
                || msg.contains("suggest") || msg.contains("best");
    }
}
