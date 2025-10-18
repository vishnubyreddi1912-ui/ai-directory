package com.aihub.directory.controllers;

import com.aihub.directory.dto.AiToolDto;
import com.aihub.directory.entities.AiTool;
import com.aihub.directory.mapper.AiToolMapper;
import com.aihub.directory.repositories.AiToolRepository;
import com.aihub.directory.services.AiToolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/ai-tools")
public class AiToolController {

    @Autowired
    private AiToolRepository aiToolRepository;


    @Autowired
    private AiToolService aiToolService;


    // ✅ Fetch AI tool by name
    @GetMapping("/name/{name}")
    public ResponseEntity<?> getAiToolByName(@PathVariable String name) {
        Optional<AiTool> toolOpt = aiToolRepository.findByNameIgnoreCase(name);
        if (toolOpt.isEmpty()) {
            return ResponseEntity.status(404).body("❌ AI Tool not found: " + name);
        }

        AiToolDto dto = AiToolMapper.toDtoforAi(toolOpt.get());
        return ResponseEntity.ok(dto);
    }

    @GetMapping("/search")
    public ResponseEntity<List<AiToolDto>> searchTools(@RequestParam String query) {
        // For now, fetch all and filter in-memory (can optimize later with LIKE)
        List<AiToolDto> all = aiToolService.getAllTools();
        List<AiToolDto> filtered = all.stream()
                .filter(tool -> tool.getName().toLowerCase().contains(query.toLowerCase()))
                .toList();
        return ResponseEntity.ok(filtered);
    }

    // ⚖️ Compare two tools
    @GetMapping("/compare")
    public ResponseEntity<Map<String, AiToolDto>> compareTools(
            @RequestParam String tool1,
            @RequestParam String tool2) {

        Optional<AiToolDto> left = aiToolService.getToolByName(tool1);
        Optional<AiToolDto> right = aiToolService.getToolByName(tool2);

        if (left.isEmpty() || right.isEmpty()) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", null));
        }

        Map<String, AiToolDto> result = new HashMap<>();
        result.put("left", left.get());
        result.put("right", right.get());

        return ResponseEntity.ok(result);
    }
}
