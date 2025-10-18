package com.aihub.directory.importcontrollers;

import com.aihub.directory.importservice.AiToolImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/ai-tools/import")
public class AiToolImportController {

    @Autowired
    private AiToolImportService aiToolImportService;

    // ✅ Health check endpoint
    @GetMapping("/health")
    public ResponseEntity<String> healthCheck() {
        return ResponseEntity.ok("✅ AI Directory backend is up and running!");
    }

    // ✅ Import JSON endpoint
    /**
     * Example JSON:
     * [
     *   {
     *     "name": "ChatGPT",
     *     "description": "AI chatbot by OpenAI",
     *     "release_date": "2022-11-30",
     *     "website_url": "https://chat.openai.com",
     *     "pricing_model": "Freemium",
     *     "has_free_plan": true,
     *     "has_premium_plan": true,
     *     "free_features_summary": "Basic conversation",
     *     "premium_features_summary": "Faster responses, GPT-4 access",
     *     "starting_price": "20",
     *     "category_id": 5
     *   }
     * ]
     */
    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> importAiTools(@RequestBody String jsonData) {
        if (jsonData == null || jsonData.isBlank()) {
            return ResponseEntity.badRequest().body("❌ Please upload valid JSON data!");
        }

        String result = aiToolImportService.importJson(jsonData);
        return ResponseEntity.ok(result);
    }
}
