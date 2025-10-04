package com.aihub.directory.importservice;

import com.aihub.directory.entities.AiTool;
import com.aihub.directory.entities.ProCon;
import com.aihub.directory.repositories.AiToolRepository;
import com.aihub.directory.repositories.ProConRepository;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.InputStream;
import java.util.*;

@Service
public class ProConImportService {

    @Autowired
    private ProConRepository proConRepository;

    @Autowired
    private AiToolRepository aiToolRepository;

    public String importExcel(MultipartFile file) {
        try (InputStream is = file.getInputStream();
             Workbook workbook = new XSSFWorkbook(is)) {

            Sheet sheet = workbook.getSheet("Pros_Cons");
            if (sheet == null) {
                return "❌ Sheet 'Pros_Cons' not found in Excel file!";
            }

            Iterator<Row> rowIterator = sheet.iterator();
            if (rowIterator.hasNext()) rowIterator.next(); // skip header

            int importedCount = 0;
            while (rowIterator.hasNext()) {
                Row row = rowIterator.next();

                String aiName = getCellValue(row.getCell(1)); // ai_name column
                String type = getCellValue(row.getCell(2)); // Pro/Con
                String content = getCellValue(row.getCell(3)); // content

                if (aiName.isBlank() || content.isBlank()) continue;

                Optional<AiTool> aiToolOpt = aiToolRepository.findByName(aiName);
                if (aiToolOpt.isEmpty()) {
                    System.err.println("⚠ AI tool not found for pros/cons: " + aiName);
                    continue;
                }

                ProCon proCon = new ProCon();
                proCon.setAiTool(aiToolOpt.get());
                proCon.setType(type);
                proCon.setContent(content);

                proConRepository.save(proCon);
                importedCount++;
            }

            return "✅ Imported " + importedCount + " pros/cons successfully.";

        } catch (Exception e) {
            e.printStackTrace();
            return "❌ Error importing pros/cons: " + e.getMessage();
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
