package com.aihub.directory.importservice;

import com.aihub.directory.entities.AiTool;
import com.aihub.directory.entities.ProCon;
import com.aihub.directory.repositories.AiToolRepository;
import com.aihub.directory.repositories.ProConRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class ProConImportService {

    @Autowired
    private ProConRepository proConRepository;

    @Autowired
    private AiToolRepository aiToolRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public String importJson(String jsonData) {
        try {
            JsonNode root = objectMapper.readTree(jsonData);
            if (!root.isArray()) {
                return "❌ JSON must be an array of pros/cons!";
            }

            int importedCount = 0;
            List<String> errors = new ArrayList<>();

            int index = 0;
            for (JsonNode node : root) {
                index++;

                String aiName = getText(node, "ai_name");
                String type = getText(node, "type");
                String content = getText(node, "content");

                // --- Basic validation ---
                if (aiName.isBlank()) {
                    errors.add("Row " + index + ": Missing 'ai_name'.");
                    continue;
                }
                if (type.isBlank()) {
                    errors.add("Row " + index + ": Missing 'type' (Pro/Con).");
                    continue;
                }
                if (content.isBlank()) {
                    errors.add("Row " + index + ": Missing 'content'.");
                    continue;
                }

                Optional<AiTool> aiToolOpt = aiToolRepository.findByName(aiName);
                if (aiToolOpt.isEmpty()) {
                    errors.add("Row " + index + ": AI tool not found - '" + aiName + "'.");
                    continue;
                }

                try {
                    ProCon proCon = new ProCon();
                    proCon.setAiTool(aiToolOpt.get());
                    proCon.setType(type);
                    proCon.setContent(content);

                    proConRepository.save(proCon);
                    importedCount++;

                } catch (Exception e) {
                    errors.add("Row " + index + ": Failed to save (" + content + ") → " + e.getMessage());
                }
            }

            // --- Build summary ---
            StringBuilder result = new StringBuilder();
            result.append("✅ Successfully imported ").append(importedCount).append(" pros/cons.\n");
            if (!errors.isEmpty()) {
                result.append("⚠ Errors (").append(errors.size()).append("):\n");
                for (String err : errors) {
                    result.append(" - ").append(err).append("\n");
                }
            } else {
                result.append("🎉 No errors found!");
            }

            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error importing pros/cons: " + e.getMessage();
        }
    }

    private String getText(JsonNode node, String key) {
        JsonNode val = node.path(key);
        return val.isMissingNode() ? "" : val.asText().trim();
    }
}
