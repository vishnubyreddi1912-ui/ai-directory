package com.aihub.directory.importservice;

import com.aihub.directory.entities.AiTool;
import com.aihub.directory.entities.Feature;
import com.aihub.directory.repositories.AiToolRepository;
import com.aihub.directory.repositories.FeatureRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class FeatureImportService {

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private AiToolRepository aiToolRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public String importJson(String jsonData) {
        try {
            JsonNode root = objectMapper.readTree(jsonData);
            if (!root.isArray()) {
                return "❌ JSON must be an array of features!";
            }

            int importedCount = 0;
            List<String> errors = new ArrayList<>();

            int index = 0;
            for (JsonNode node : root) {
                index++; // 1-based position tracking

                String aiName = getText(node, "ai_name");
                String featureName = getText(node, "feature_name");
                boolean paid = node.path("paid").asBoolean(false);

                // --- Validation checks ---
                if (aiName.isBlank()) {
                    errors.add("Row " + index + ": Missing 'ai_name'.");
                    continue;
                }
                if (featureName.isBlank()) {
                    errors.add("Row " + index + ": Missing 'feature_name'.");
                    continue;
                }

                Optional<AiTool> aiToolOpt = aiToolRepository.findByName(aiName);
                if (aiToolOpt.isEmpty()) {
                    errors.add("Row " + index + ": AI tool not found - '" + aiName + "'.");
                    continue;
                }

                try {
                    Feature feature = new Feature();
                    feature.setAiTool(aiToolOpt.get());
                    feature.setFeatureName(featureName);
                    feature.setPaid(paid);

                    featureRepository.save(feature);
                    importedCount++;

                } catch (Exception e) {
                    errors.add("Row " + index + ": Failed to save feature '" + featureName + "' → " + e.getMessage());
                }
            }

            // --- Build result summary ---
            StringBuilder result = new StringBuilder();
            result.append("✅ Successfully imported ").append(importedCount).append(" features.\n");
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
            return "❌ Error importing features: " + e.getMessage();
        }
    }

    private String getText(JsonNode node, String key) {
        JsonNode val = node.path(key);
        return val.isMissingNode() ? "" : val.asText().trim();
    }
}
