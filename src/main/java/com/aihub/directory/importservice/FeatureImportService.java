package com.aihub.directory.importservice;

import com.aihub.directory.entities.AiTool;
import com.aihub.directory.entities.Feature;
import com.aihub.directory.repositories.AiToolRepository;
import com.aihub.directory.repositories.FeatureRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
public class FeatureImportService {

    @Autowired
    private FeatureRepository featureRepository;

    @Autowired
    private AiToolRepository aiToolRepository;

    public String importExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheet("Features");
            if (sheet == null) {
                return "❌ Sheet 'Features' not found in Excel file!";
            }

            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) rowIterator.next(); // skip header

            int importedCount = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                String aiName = getCellValue(row.getCell(1)); // ai_name column
                String featureName = getCellValue(row.getCell(2)); // feature_name
                String paidStr = getCellValue(row.getCell(3)); // paid

                if (aiName.isBlank() || featureName.isBlank()) continue;

                Optional<AiTool> aiToolOpt = aiToolRepository.findByName(aiName);
                if (aiToolOpt.isEmpty()) {
                    System.err.println("⚠ AI tool not found for feature: " + aiName);
                    continue;
                }

                Feature feature = new Feature();
                feature.setAiTool(aiToolOpt.get());
                feature.setFeatureName(featureName);
                feature.setPaid(Boolean.parseBoolean(paidStr));

                featureRepository.save(feature);
                importedCount++;
            }

            StringBuilder result = new StringBuilder("✅ Successfully imported Features.\n");
            return result.toString();

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error importing features: " + e.getMessage();
        }
    }

    private String getCellValue(Cell cell) {
        if (cell == null) return "";
        if (cell.getCellType() == CellType.NUMERIC)
            return String.valueOf((long) cell.getNumericCellValue());
        if (cell.getCellType() == CellType.BOOLEAN)
            return String.valueOf(cell.getBooleanCellValue());
        return cell.getStringCellValue().trim();
    }
}
