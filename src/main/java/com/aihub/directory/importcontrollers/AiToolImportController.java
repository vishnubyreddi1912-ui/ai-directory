package com.aihub.directory.importcontrollers;

import com.aihub.directory.importservice.AiToolImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

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

    // ✅ Import Excel endpoint
    @PostMapping
    public ResponseEntity<String> importAiTools(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Please upload a valid Excel file!");
        }

        String result = aiToolImportService.importExcel(file);
        return ResponseEntity.ok(result);
    }
}
