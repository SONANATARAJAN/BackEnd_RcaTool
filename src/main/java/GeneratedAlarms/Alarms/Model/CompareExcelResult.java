package GeneratedAlarms.Alarms.Model;
import java.util.List;
import java.util.Map;


public class CompareExcelResult {

    private Map<String, String> row; // Holds column name → value
    private String status;
    public CompareExcelResult(Map<String, String> row, String status) {
        this.row = row;
        this.status = status;
    }

    public Map<String, String> getRow() {
        return row;
    }

    public void setRow(Map<String, String> row) {
        this.row = row;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}


