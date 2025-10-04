package com.aihub.directory.importcontrollers;

import com.aihub.directory.importservice.ProConImportService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/proscons/import")
public class ProConImportController {

    @Autowired
    private ProConImportService proConImportService;

    @PostMapping
    public ResponseEntity<String> importProsCons(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body("❌ Please upload a valid Excel file!");
        }
        String result = proConImportService.importExcel(file);
        return ResponseEntity.ok(result);
    }
}
