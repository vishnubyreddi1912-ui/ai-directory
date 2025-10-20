package com.aihub.directory.aicontrollers;


import com.aihub.directory.entities.AiTool;
import com.aihub.directory.entities.Category;
import com.aihub.directory.entities.Feature;
import com.aihub.directory.entities.ProCon;
import com.aihub.directory.repositories.AiToolRepository;
import com.aihub.directory.repositories.CategoryRepository;
import com.aihub.directory.repositories.FeatureRepository;
import com.aihub.directory.repositories.ProConRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@RestController
@RequestMapping("/api/ai/import")
@CrossOrigin(origins = {
        "http://localhost:4500",
        "https://ai-directory-1.onrender.com"
})
public class importAiController {
    @Value("${groq.api.key}")
    private String groqApiKey;

    private static final String GROQ_URL = "https://api.groq.com/openai/v1/chat/completions";

    @Autowired
    private AiToolRepository aiToolRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private ProConRepository proConRepository;

    private static final String systemPrompt = """
        You are an intelligent assistant for the AIHub Directory system.

        Your goal is to generate structured JSON ready for database insertion.

        Each AI tool must have:
        - Tool details
        - Features
        - Pros and Cons
        - A valid categoryId from the provided live category list

        Categories:
        [
          {"id":1,"name":"Chatbots"},
          {"id":2,"name":"Image Generation"},
          {"id":3,"name":"Video Generation"},
          {"id":4,"name":"Coding Assistant"},
          {"id":5,"name":"Productivity"},
          {"id":6,"name":"Education & Learning"}
        ]

        ⚙️ JSON structure:
        {
          "name": "Tool Name",
          "description": "Short, professional description",
          "releaseDate": "yyyy-MM-dd or null",
          "websiteUrl": "https://...",
          "pricingModel": "Free | Freemium | Premium",
          "hasFreePlan": true/false,
          "hasPremiumPlan": true/false,
          "freeFeaturesSummary": "Summary of free features",
          "premiumFeaturesSummary": "Summary of premium features",
          "startingPrice": "$10/month or null",
          "categoryId": <numeric>,
          "features": [
            {"featureName": "Feature 1", "paid": true/false},
            {"featureName": "Feature 2", "paid": true/false}
          ],
          "prosCons": [
            {"type": "Pro", "content": "Clear positive statement"},
            {"type": "Con", "content": "Clear limitation"}
          ]
        }

        If the tool does not fit any category, return:
        { "error": "This tool does not match any existing category." }

        🚫 Output only valid JSON.
        No markdown, no explanations, no text before or after the JSON.
        Response must start with '{' and end with '}'.
        """;

    @PostMapping
    public ResponseEntity<Map<String, Object>> chat(@RequestBody Map<String, String> payload) {
        String userMessage = payload.get("message");
        RestTemplate restTemplate = new RestTemplate();

        // 🔹 Fetch live categories
        String liveData = "Could not fetch categories.";
        try {
            ResponseEntity<String> res = restTemplate.getForEntity(
                    "https://ai-directory-1.onrender.com/api/categories",
                    String.class
            );
            liveData = res.getBody();
        } catch (Exception e) {
            System.out.println("⚠️ Live category fetch failed: " + e.getMessage());
        }

        // 🔹 Prepare Groq request
        Map<String, Object> body = new LinkedHashMap<>();
        body.put("model", "llama-3.1-8b-instant");
        body.put("temperature", 0.4);

        List<Map<String, String>> messages = new ArrayList<>();
        messages.add(Map.of("role", "system", "content", systemPrompt));
        messages.add(Map.of("role", "system", "content", "Here is live category data: " + liveData));
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
            String aiContent = (String) message.get("content");

            // 🧹 Clean the AI response
            String cleanContent = aiContent
                    .replaceAll("(?s)^.*?(\\{)", "{")
                    .replaceAll("(?s)(\\})[^\\}]*$", "}")
                    .trim();

            System.out.println("🧠 Cleaned AI JSON:\n" + cleanContent);

            // ✅ Parse JSON safely
            ObjectMapper mapper = new ObjectMapper();
            JsonNode json;
            try {
                json = mapper.readTree(cleanContent);
            } catch (Exception parseEx) {
                System.err.println("❌ JSON parsing failed. Raw output:\n" + aiContent);
                return ResponseEntity.badRequest().body(Map.of(
                        "reply", "⚠️ The AI returned invalid JSON.",
                        "rawOutput", aiContent
                ));
            }

            if (json.has("error")) {
                return ResponseEntity.ok(Map.of("reply", json.get("error").asText()));
            }

            // ✅ Create and save AiTool
            AiTool tool = new AiTool();
            tool.setName(json.path("name").asText());
            tool.setDescription(json.path("description").asText(null));
            if (!json.path("releaseDate").isNull() && !json.path("releaseDate").asText().equals("null")) {
                try {
                    tool.setReleaseDate(LocalDate.parse(json.path("releaseDate").asText()));
                } catch (Exception ignored) {}
            }

            tool.setWebsiteUrl(json.path("websiteUrl").asText(null));
            tool.setPricingModel(json.path("pricingModel").asText(null));
            tool.setHasFreePlan(json.path("hasFreePlan").asBoolean(false));
            tool.setHasPremiumPlan(json.path("hasPremiumPlan").asBoolean(false));
            tool.setFreeFeaturesSummary(json.path("freeFeaturesSummary").asText(null));
            tool.setPremiumFeaturesSummary(json.path("premiumFeaturesSummary").asText(null));
            tool.setStartingPrice(json.path("startingPrice").asText(null));

            long categoryId = json.path("categoryId").asLong(0);
            Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
            if (categoryOpt.isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of("reply", "❌ Invalid category ID: " + categoryId));
            }
            tool.setCategory(categoryOpt.get());

            AiTool savedTool = aiToolRepository.save(tool);

            // ✅ Save Features
            List<Feature> savedFeatures = new ArrayList<>();
            if (json.has("features") && json.get("features").isArray()) {
                for (JsonNode featureNode : json.get("features")) {
                    String featureName = featureNode.path("featureName").asText("");
                    boolean paid = featureNode.path("paid").asBoolean(false);
                    if (!featureName.isBlank()) {
                        Feature feature = new Feature();
                        feature.setAiTool(savedTool);
                        feature.setFeatureName(featureName);
                        feature.setPaid(paid);
                        savedFeatures.add(featureRepository.save(feature));
                    }
                }
            }

            // ✅ Save Pros & Cons
            List<ProCon> savedProsCons = new ArrayList<>();
            if (json.has("prosCons") && json.get("prosCons").isArray()) {
                for (JsonNode proConNode : json.get("prosCons")) {
                    String type = proConNode.path("type").asText("");
                    String content = proConNode.path("content").asText("");
                    if (!type.isBlank() && !content.isBlank()) {
                        ProCon proCon = new ProCon();
                        proCon.setAiTool(savedTool);
                        proCon.setType(type);
                        proCon.setContent(content);
                        savedProsCons.add(proConRepository.save(proCon));
                    }
                }
            }

            // ✅ Return final response
            return ResponseEntity.ok(Map.of(
                    "reply", "✅ Tool and related data inserted successfully!",
                    "tool", savedTool,
                    "featuresInserted", savedFeatures.size(),
                    "prosConsInserted", savedProsCons.size(),
                    "features", savedFeatures,
                    "prosCons", savedProsCons
            ));

        } catch (HttpClientErrorException e) {
            System.err.println("❌ Groq API error: " + e.getResponseBodyAsString());
            return ResponseEntity.status(e.getStatusCode())
                    .body(Map.of("reply", "⚠️ Groq API Error: " + e.getResponseBodyAsString()));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500)
                    .body(Map.of("reply", "⚠️ Internal server error: " + e.getMessage()));
        }
    }
}
