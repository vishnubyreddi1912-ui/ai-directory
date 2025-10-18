package com.aihub.directory.importservice;

import com.aihub.directory.entities.AiTool;
import com.aihub.directory.entities.Category;
import com.aihub.directory.repositories.AiToolRepository;
import com.aihub.directory.repositories.CategoryRepository;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AiToolImportService {

    @Autowired
    private AiToolRepository aiToolRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ObjectMapper objectMapper;

    public String importJson(String jsonData) {
        try {
            JsonNode root = objectMapper.readTree(jsonData);
            if (!root.isArray()) {
                return "❌ JSON must be an array of AI tools!";
            }

            int importedCount = 0;
            List<String> errors = new ArrayList<>();
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

            int index = 0;
            for (JsonNode node : root) {
                index++;

                try {
                    AiTool aiTool = new AiTool();

                    // Extract fields
                    String name = getText(node, "name");
                    String description = getText(node, "description");
                    String releaseDateStr = getText(node, "release_date");
                    String websiteUrl = getText(node, "website_url");
                    String pricingModel = getText(node, "pricing_model");
                    String freeSummary = getText(node, "free_features_summary");
                    String premiumSummary = getText(node, "premium_features_summary");
                    String priceStr = getText(node, "starting_price");
                    Long categoryId = node.path("category_id").isMissingNode() ? null : node.path("category_id").asLong();
                    boolean hasFreePlan = node.path("has_free_plan").asBoolean(false);
                    boolean hasPremiumPlan = node.path("has_premium_plan").asBoolean(false);

                    // ✅ Validation
                    if (name.isBlank()) {
                        errors.add("Row " + index + ": Missing AI tool name.");
                        continue;
                    }
                    if (pricingModel.isBlank()) {
                        errors.add("Row " + index + " (" + name + "): Missing pricing_model.");
                        continue;
                    }

                    aiTool.setName(name);
                    aiTool.setDescription(description);
                    aiTool.setWebsiteUrl(websiteUrl);
                    aiTool.setPricingModel(pricingModel);
                    aiTool.setHasFreePlan(hasFreePlan);
                    aiTool.setHasPremiumPlan(hasPremiumPlan);
                    aiTool.setFreeFeaturesSummary(freeSummary);
                    aiTool.setPremiumFeaturesSummary(premiumSummary);

                    // ✅ Parse release_date
                    if (!releaseDateStr.isBlank()) {
                        try {
                            aiTool.setReleaseDate(LocalDate.parse(releaseDateStr, formatter));
                        } catch (Exception e) {
                            errors.add("Row " + index + " (" + name + "): Invalid release_date '" + releaseDateStr + "'. Expected format: yyyy-MM-dd.");
                        }
                    }

                    // ✅ Parse category
                    if (categoryId != null && categoryId > 0) {
                        Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
                        if (categoryOpt.isPresent()) {
                            aiTool.setCategory(categoryOpt.get());
                        } else {
                            errors.add("Row " + index + " (" + name + "): Category ID " + categoryId + " not found.");
                        }
                    } else {
                        errors.add("Row " + index + " (" + name + "): Category ID missing or invalid.");
                    }

                    // ✅ Save AI Tool
                    aiToolRepository.save(aiTool);
                    importedCount++;

                } catch (Exception e) {
                    errors.add("Row " + index + ": Unexpected error → " + e.getMessage());
                }
            }

            // ✅ Build result summary
            StringBuilder result = new StringBuilder();
            result.append("✅ Successfully imported ").append(importedCount).append(" AI tools.\n");
            if (!errors.isEmpty()) {
                result.append("⚠️ Issues (").append(errors.size()).append("):\n");
                for (String err : errors) {
                    result.append(" - ").append(err).append("\n");
                }
            } else {
                result.append("🎉 All records imported successfully!");
            }

            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error importing data: " + e.getMessage();
        }
    }

    private String getText(JsonNode node, String key) {
        JsonNode val = node.path(key);
        return val.isMissingNode() ? "" : val.asText().trim();
    }
}
