package com.aihub.directory.importservice;

import com.aihub.directory.entities.AiTool;
import com.aihub.directory.entities.Category;
import com.aihub.directory.repositories.AiToolRepository;
import com.aihub.directory.repositories.CategoryRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

@Service
public class AiToolImportService {

    @Autowired
    private AiToolRepository aiToolRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    public String importExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheet("AI_Tools");

            if (sheet == null) {
                return "❌ Sheet 'AI_Tools' not found in Excel file!";
            }

            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) rowIterator.next(); // skip header

            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            List<String> errors = new ArrayList<>();
            int successCount = 0;
            int rowNum = 1; // start after header

            while (rowIterator.hasNext()) {
                rowNum++;
                Row row = rowIterator.next();

                try {
                    AiTool aiTool = new AiTool();

                    String name = getCellValue(row.getCell(1));
                    String description = getCellValue(row.getCell(2));
                    String releaseDateStr = getCellValue(row.getCell(3));
                    String websiteUrl = getCellValue(row.getCell(4));
                    String pricingModel = getCellValue(row.getCell(5));
                    String hasFreePlanStr = getCellValue(row.getCell(6));
                    String hasPremiumPlanStr = getCellValue(row.getCell(7));
                    String freeFeaturesSummary = getCellValue(row.getCell(8));
                    String premiumFeaturesSummary = getCellValue(row.getCell(9));
                    String priceStr = getCellValue(row.getCell(10));
                    String categoryIdStr = getCellValue(row.getCell(11));

                    // ✅ Validate required fields
                    if (name.isBlank()) {
                        errors.add("Row " + rowNum + ": Missing AI name.");
                        continue;
                    }
                    if (pricingModel.isBlank()) {
                        errors.add("Row " + rowNum + " (" + name + "): Missing pricing_model.");
                        continue;
                    }

                    // Set fields
                    aiTool.setName(name);
                    aiTool.setDescription(description);
                    aiTool.setWebsiteUrl(websiteUrl);
                    aiTool.setPricingModel(pricingModel);
                    aiTool.setHasFreePlan(Boolean.parseBoolean(hasFreePlanStr));
                    aiTool.setHasPremiumPlan(Boolean.parseBoolean(hasPremiumPlanStr));
                    aiTool.setFreeFeaturesSummary(freeFeaturesSummary);
                    aiTool.setPremiumFeaturesSummary(premiumFeaturesSummary);

                    // Parse release_date
                    if (!releaseDateStr.isBlank()) {
                        try {
                            aiTool.setReleaseDate(LocalDate.parse(releaseDateStr, formatter));
                        } catch (Exception e) {
                            errors.add("Row " + rowNum + " (" + name + "): Invalid release_date '" + releaseDateStr + "'. Expected yyyy-MM-dd.");
                        }
                    }

                    // Parse price
                    if (!priceStr.isBlank()) {
                        try {
                            aiTool.setStartingPrice(new BigDecimal(priceStr));
                        } catch (NumberFormatException e) {
                            errors.add("Row " + rowNum + " (" + name + "): Invalid price '" + priceStr + "'.");
                        }
                    }

                    // Category lookup
                    if (!categoryIdStr.isBlank()) {
                        try {
                            Long categoryId = Long.parseLong(categoryIdStr);
                            Optional<Category> categoryOpt = categoryRepository.findById(categoryId);
                            if (categoryOpt.isPresent()) {
                                aiTool.setCategory(categoryOpt.get());
                            } else {
                                errors.add("Row " + rowNum + " (" + name + "): Category ID " + categoryId + " not found.");
                            }
                        } catch (NumberFormatException e) {
                            errors.add("Row " + rowNum + " (" + name + "): Invalid category ID '" + categoryIdStr + "'.");
                        }
                    } else {
                        errors.add("Row " + rowNum + " (" + name + "): Category ID is missing.");
                    }

                    aiToolRepository.save(aiTool);
                    successCount++;

                } catch (Exception ex) {
                    errors.add("Row " + rowNum + ": Unexpected error - " + ex.getMessage());
                }
            }

            // ✅ Prepare final summary message
            StringBuilder result = new StringBuilder("✅ Successfully imported " + successCount + " AI Tools.\n");
            if (!errors.isEmpty()) {
                result.append("\n⚠️ Issues found in ").append(errors.size()).append(" rows:\n");
                for (String err : errors) {
                    result.append("- ").append(err).append("\n");
                }
            }

            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error importing data: " + e.getMessage();
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC) {
            return String.valueOf((long) cell.getNumericCellValue());
        } else if (cell.getCellType() == CellType.BOOLEAN) {
            return String.valueOf(cell.getBooleanCellValue());
        } else {
            return cell.getStringCellValue().trim();
        }
    }
}
