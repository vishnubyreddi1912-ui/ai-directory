package com.aihub.directory.aicontrollers;

import com.aihub.directory.entities.AiTool;
import com.aihub.directory.entities.Category;
import com.aihub.directory.entities.Feature;
import com.aihub.directory.entities.ProCon;
import com.aihub.directory.repositories.AiToolRepository;
import com.aihub.directory.repositories.CategoryRepository;
import com.aihub.directory.repositories.FeatureRepository;
import com.aihub.directory.repositories.ProConRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = {
        "http://localhost:4200",
        "https://ai-directory-1.onrender.com"
})
public class ChatController {
//
//    @Value("${groq.api.key}")
//    private String groqApiKey;
//
//    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";
//
//    @Autowired private AiToolRepository aiToolRepository;
//    @Autowired private CategoryRepository categoryRepository;
//    @Autowired private FeatureRepository featureRepository;
//    @Autowired private ProConRepository proConRepository;
//
//    /**
//     * 🧠 System prompt that allows mixed (human + JSON) replies.
//     */
//    private static final String systemPrompt = """
//        You are "AIHub Assistant" — a friendly and smart guide for users exploring AI tools in the AIHub Directory.
//
//        You have full access to the user's local AI tools database (provided below).
//        You cannot invent new tools — only use the provided ones.
//
//        🧩 Your behavior:
//        - If the user asks casually ("Hey what’s trending?", "Tell me about some cool tools"),
//          respond like a human assistant with natural conversation and maybe light humor or excitement.
//        - If the user explicitly asks for a list, comparison, or best tools for a task,
//          respond with structured JSON (see below).
//        - You can sometimes mix both (chat + JSON) but make sure any JSON part starts
//          from `{` and ends with `}` so the frontend can parse it.
//
//        ⚙️ JSON format (for UI rendering):
//        {
//          "type": "ai_tools",
//          "items": [
//            {
//              "id": 1,
//              "name": "Tool Name",
//              "description": "Short and clear description",
//              "category": "Category Name",
//              "rating": 4.8,
//              "reason": "Why it's a great match",
//              "websiteUrl": "https://...",
//              "pricingModel": "Free | Freemium | Premium",
//              "pros": ["Positive", "Another positive"],
//              "cons": ["Limitation 1"],
//              "features": ["Feature 1", "Feature 2"]
//            }
//          ]
//        }
//
//        🧭 Rules:
//        - Always rely only on the provided data.
//        - You may include human conversation before or after the JSON.
//        - If nothing fits the query, say: "Hmm, I couldn’t find anything perfect — but here’s what comes close!".
//        - Never include markdown, asterisks, or code fences.
//        - main point If you are give json never add any text to it. only pure format what I gave
//        """;
//
//    @PostMapping
//    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> payload) {
//        String userMessage = payload.get("message");
//        RestTemplate restTemplate = new RestTemplate();
//
//        // 🔹 1. Collect all DB data
//        List<AiTool> tools = aiToolRepository.findAll();
//        List<Feature> features = featureRepository.findAll();
//        List<ProCon> prosCons = proConRepository.findAll();
//
//        // 🔹 2. Prepare snapshot for AI
//        List<Map<String, Object>> toolData = new ArrayList<>();
//        for (AiTool tool : tools) {
//            Map<String, Object> map = new LinkedHashMap<>();
//            map.put("id", tool.getId());
//            map.put("name", tool.getName());
//            map.put("description", tool.getDescription());
//            map.put("category", tool.getCategory() != null ? tool.getCategory().getName() : "Unknown");
//            map.put("pricingModel", tool.getPricingModel());
//            map.put("websiteUrl", tool.getWebsiteUrl());
//
//            List<String> featureList = features.stream()
//                    .filter(f -> f.getAiTool().getId().equals(tool.getId()))
//                    .map(Feature::getFeatureName)
//                    .collect(Collectors.toList());
//
//            List<String> prosList = prosCons.stream()
//                    .filter(p -> p.getAiTool().getId().equals(tool.getId()) && "Pro".equalsIgnoreCase(p.getType()))
//                    .map(ProCon::getContent)
//                    .collect(Collectors.toList());
//
//            List<String> consList = prosCons.stream()
//                    .filter(p -> p.getAiTool().getId().equals(tool.getId()) && "Con".equalsIgnoreCase(p.getType()))
//                    .map(ProCon::getContent)
//                    .collect(Collectors.toList());
//
//            map.put("features", featureList);
//            map.put("pros", prosList);
//            map.put("cons", consList);
//
//            toolData.add(map);
//        }
//
//        // 🔹 3. Send to Groq
//        Map<String, Object> body = new LinkedHashMap<>();
//        body.put("model", "llama-3.1-8b-instant");
//        body.put("temperature", 0.6); // slightly higher for creativity
//
//        List<Map<String, String>> messages = new ArrayList<>();
//        messages.add(Map.of("role", "system", "content", systemPrompt));
//        messages.add(Map.of("role", "system", "content", "Here is the local database snapshot: " + toolData));
//        messages.add(Map.of("role", "user", "content", userMessage));
//        body.put("messages", messages);
//
//        HttpHeaders headers = new HttpHeaders();
//        headers.set("Authorization", "Bearer " + groqApiKey);
//        headers.setContentType(MediaType.APPLICATION_JSON);
//        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(body, headers);
//
//        try {
//            ResponseEntity<Map> response = restTemplate.postForEntity(GROQ_URL, entity, Map.class);
//            Map<String, Object> choice = (Map<String, Object>) ((List<?>) response.getBody().get("choices")).get(0);
//            Map<String, Object> message = (Map<String, Object>) choice.get("message");
//            String aiContent = (String) message.get("content");
//
//            // 🧹 Clean & prepare response
//            String clean = aiContent.trim();
//
//            System.out.println("🤖 AIHub Assistant Raw Output:\n" + clean);
//
//            // Try to detect if it’s JSON or plain message
//            ObjectMapper mapper = new ObjectMapper();
//            boolean isJson = clean.startsWith("{") || clean.startsWith("[");
//            if (isJson) {
//                try {
//                    Map<String, Object> parsed = mapper.readValue(clean, Map.class);
//                    return ResponseEntity.ok(parsed); // structured JSON to UI
//                } catch (Exception e) {
//                    // Partial JSON or hybrid message
//                    return ResponseEntity.ok(Map.of("reply", clean));
//                }
//            } else {
//                return ResponseEntity.ok(Map.of("reply", clean)); // human-like reply
//            }
//
//        } catch (HttpClientErrorException e) {
//            System.err.println("❌ Groq API error: " + e.getResponseBodyAsString());
//            return ResponseEntity.status(e.getStatusCode())
//                    .body(Map.of("reply", "⚠️ Groq API Error: " + e.getResponseBodyAsString()));
//        } catch (Exception e) {
//            e.printStackTrace();
//            return ResponseEntity.status(500)
//                    .body(Map.of("reply", "⚠️ Internal server error: " + e.getMessage()));
//        }
//    }
}
