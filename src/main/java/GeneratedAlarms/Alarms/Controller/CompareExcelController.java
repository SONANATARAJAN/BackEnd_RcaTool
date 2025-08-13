package GeneratedAlarms.Alarms.Controller;

import GeneratedAlarms.Alarms.Model.CompareExcelResult;
import GeneratedAlarms.Alarms.Repository.DatabaseExcelService;
import GeneratedAlarms.Alarms.Service.ExcelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api")
public class CompareExcelController {

    @Autowired
    private ExcelService excelService;

    @Autowired
    private DatabaseExcelService dbService;

    private static final String[] COLUMN_NAMES = {
            "R1_OBJTYPE", "R1_ETSUK", "R1_MRMTID", "R1_REFEROBJTYPE", "R1_SOURCEOFRCI","R1_ADDINFO",
            "R2_OBJTYPE", "R2_ETSUK", "R2_MRMTID", "R2_REFEROBJTYPE", "R2_SOURCEOFRCI","R2_ADDINFO",
            "SRCOFRELATIONSHIP", "RELATIONSHIPTYPE"
    };
    @GetMapping("/compare")
    public ResponseEntity<List<CompareExcelResult>> compareWithExcel(@RequestParam String dbName) throws Exception {

        // Fetch DB data once
        List<List<String>> dbData = dbService.fetchDbData(dbName);

        // Normalize DB rows
        List<String> normalizedDbRows = dbData.stream()
                .map(this::normalizeRow)
                .collect(Collectors.toList());

        // Read Excel data once, skip header, and normalize
        List<List<String>> excelData = excelService.readExcelFromClasspath("data/RCA_RELATIONSHIP.xlsx");
        List<List<String>> excelRows = excelData.subList(1, excelData.size());
        List<String> normalizedExcelRows = excelRows.stream()
                .map(this::normalizeRow)
                .collect(Collectors.toList());

        // Debug: Print raw and normalized data for last column
        System.out.println("=== DEBUG: Raw and Normalized DB vs Excel Data ===");
        for (int i = 0; i < Math.min(5, dbData.size()); i++) {
            List<?> dbRow = dbData.get(i);
            List<?> excelRow = (i < excelRows.size()) ? excelRows.get(i) : Collections.emptyList();
            String dbNorm = normalizedDbRows.get(i);
            String excelNorm = (i < normalizedExcelRows.size()) ? normalizedExcelRows.get(i) : "";

            System.out.println("DB RAW    : " + dbRow);
            System.out.println("Excel RAW : " + excelRow);
            System.out.println("DB NORM   : [" + dbNorm + "] len=" + dbNorm.length());
            System.out.println("Excel NORM: [" + excelNorm + "] len=" + excelNorm.length());

            for (int k = 0; k < Math.max(dbNorm.length(), excelNorm.length()); k++) {
                char dbChar = k < dbNorm.length() ? dbNorm.charAt(k) : ' ';
                char xlChar = k < excelNorm.length() ? excelNorm.charAt(k) : ' ';
               /* if (dbChar != xlChar) {
                    System.out.println("Mismatch at index " + k +
                            " DB='" + (int) dbChar + "' XL='" + (int) xlChar + "'");
                }*/
            /*    for (int col = 0; col < COLUMN_NAMES.length; col++) {
                    String dbVal = (col < dbRow.size() && dbRow.get(col) != null) ? dbRow.get(col).toString() : "";
                    String xlVal = (col < excelRow.size() && excelRow.get(col) != null) ? excelRow.get(col).toString() : "";
                    System.out.println("Col " + col + " (" + COLUMN_NAMES[col] + ") DB='" + dbVal + "' Excel='" + xlVal + "'");
                }*/

            }

            System.out.println("-------------------------------------------");
        }

        List<CompareExcelResult> results = new ArrayList<>();

        // Compare DB rows against Excel rows
        for (int i = 0; i < normalizedDbRows.size(); i++) {
            String dbRowStr = normalizedDbRows.get(i);
            boolean matchFound = normalizedExcelRows.contains(dbRowStr);

            // Map original DB row for display
            List<?> originalDbRow = dbData.get(i);
            Map<String, String> mappedRow = new LinkedHashMap<>();
            for (int j = 0; j < COLUMN_NAMES.length; j++) {
                String cellValue = (j < originalDbRow.size() && originalDbRow.get(j) != null)
                        ? originalDbRow.get(j).toString()
                        : "";
                mappedRow.put(COLUMN_NAMES[j], cellValue);
            }



            results.add(new CompareExcelResult(mappedRow, matchFound ? "Pass" : "Fail"));
        }

        return ResponseEntity.ok(results);
    }

    private String normalizeValue(Object value) {
        if (value == null) return "";
        String str = value.toString();

        // Replace ALL possible invisible chars with normal space
        str = str.replaceAll("[\\u00A0\\u200B\\uFEFF]", " ");

        // Collapse spaces, trim, and lowercase
        str = str.replaceAll("\\s+", " ").trim();

        if (str.equalsIgnoreCase("null")) return "";
        return str.toLowerCase();
    }


    private String normalizeRow(List<?> row) {
        // Ensure the list matches exactly the expected number of columns
        List<?> fixedRow = new ArrayList<>(row);
        while (fixedRow.size() < COLUMN_NAMES.length) {
            ((ArrayList) fixedRow).add("");
        }
        return fixedRow.stream()
                .map(this::normalizeValue)
                .collect(Collectors.joining("|"));
    }


}
