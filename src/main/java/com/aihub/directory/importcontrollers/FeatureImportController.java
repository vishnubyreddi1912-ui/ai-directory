package com.aihub.directory.importcontrollers;

import com.aihub.directory.importservice.FeatureImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/features/import")
public class FeatureImportController {

    @Autowired
    private FeatureImportService featureImportService;

    /**
     * Example JSON input:
     * [
     *   { "ai_name": "ChatGPT", "feature_name": "Text Generation", "paid": true },
     *   { "ai_name": "Midjourney", "feature_name": "Image Creation", "paid": false }
     * ]
     */
    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> importFeatures(@RequestBody String jsonData) {
        if (jsonData == null || jsonData.isBlank()) {
            return ResponseEntity.badRequest().body("❌ Please upload valid JSON data!");
        }
        String result = featureImportService.importJson(jsonData);
        return ResponseEntity.ok(result);
    }
}
