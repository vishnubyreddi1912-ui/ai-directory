package com.aihub.directory.importcontrollers;

import com.aihub.directory.importservice.FeatureImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/features/import")
public class FeatureImportController {

    @Autowired
    private FeatureImportService featureImportService;

    @PostMapping
    public ResponseEntity<String> importFeatures(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Please upload a valid Excel file!");
        }
        String result = featureImportService.importExcel(file);
        return ResponseEntity.ok(result);
    }
}
