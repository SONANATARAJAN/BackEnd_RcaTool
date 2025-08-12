package GeneratedAlarms.Alarms.Service;

import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

@Service
public class ExcelService {
    public List<List<String>> readExcelFromClasspath(String path) throws IOException {
        List<List<String>> data = new ArrayList<>();
        InputStream inputStream = new ClassPathResource(path).getInputStream();
        Workbook workbook = new XSSFWorkbook(inputStream);
        Sheet sheet = workbook.getSheetAt(0);

        for (Row row : sheet) {
            List<String> rowData = new ArrayList<>();
            for (Cell cell : row) {
                cell.setCellType(CellType.STRING);
                rowData.add(cell.getStringCellValue().trim());
            }
            data.add(rowData);
        }

        workbook.close();
        return data;
    }
}
