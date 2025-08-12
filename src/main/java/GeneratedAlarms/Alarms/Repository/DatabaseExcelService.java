package GeneratedAlarms.Alarms.Repository;


import GeneratedAlarms.Alarms.Model.OracleDbConfig;
import GeneratedAlarms.Alarms.Service.ConfigLoaderService;
import GeneratedAlarms.Alarms.Service.OracleService;
import GeneratedAlarms.Alarms.Service.QueryXmlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class DatabaseExcelService {

    @Autowired
    private OracleService oracleService;

    @Autowired
    private ConfigLoaderService configLoaderService;

    @Autowired
    private QueryXmlService queryXmlService;

    public List<List<String>> fetchDbData(String dbName) throws Exception {
        OracleDbConfig config = configLoaderService.getConfigByName(dbName);
        if (config == null) throw new RuntimeException("Database config not found: " + dbName);

        String sql = queryXmlService.getQueryById("compareQuery");
        if (sql == null) throw new RuntimeException("Query not found in XML for ID: compareQuery");

        List<Map<String, Object>> rows = oracleService.executeQuery(config, sql, Collections.emptyMap());

        List<List<String>> result = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            List<String> values = new ArrayList<>();
            for (int i = 1; i <= 12; i++) {
                Object value = row.getOrDefault("COL" + i, "");
                values.add(value == null ? "" : value.toString().trim());
            }
            result.add(values);
        }

        return result;
    }
}
