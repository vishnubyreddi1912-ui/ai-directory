package com.aihub.directory.importcontrollers;

import com.aihub.directory.importservice.ProConImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/proscons/import")
public class ProConImportController {

    @Autowired
    private ProConImportService proConImportService;

    /**
     * Example JSON input:
     * [
     *   { "ai_name": "ChatGPT", "type": "Pro", "content": "Accurate answers" },
     *   { "ai_name": "ChatGPT", "type": "Con", "content": "Limited context memory" }
     * ]
     */
    @PostMapping(consumes = "application/json")
    public ResponseEntity<String> importProsCons(@RequestBody String jsonData) {
        if (jsonData == null || jsonData.isBlank()) {
            return ResponseEntity.badRequest().body("❌ Please upload valid JSON data!");
        }
        String result = proConImportService.importJson(jsonData);
        return ResponseEntity.ok(result);
    }
}
