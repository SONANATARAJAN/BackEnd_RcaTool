package GeneratedAlarms.Alarms.Controller;

import GeneratedAlarms.Alarms.Model.CompareExcelResult;
import GeneratedAlarms.Alarms.Model.OracleDbConfig;
import GeneratedAlarms.Alarms.Service.ConfigLoaderService;
import GeneratedAlarms.Alarms.Service.HierarchyQueryService;
import GeneratedAlarms.Alarms.Service.OracleService;
import GeneratedAlarms.Alarms.Service.QueryXmlService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/oracle")
public class OracleController {

    @Autowired
    private final ConfigLoaderService configLoaderService;

    @Autowired
    private final OracleService oracleService;

    @Autowired
    private final QueryXmlService queryXmlService;

    @Autowired
    private final HierarchyQueryService hierarchyQueryService;

    public OracleController(ConfigLoaderService configLoaderService,
                            OracleService oracleService,
                            QueryXmlService queryXmlService, HierarchyQueryService hierarchyQueryService) {
        this.configLoaderService = configLoaderService;
        this.oracleService = oracleService;
        this.queryXmlService = queryXmlService;
        this.hierarchyQueryService = hierarchyQueryService;
    }

    @GetMapping("/configs")
    public Collection<OracleDbConfig> getAllConfigs() {
        return configLoaderService.getAllConfigs().values();
    }

    @GetMapping("/fetch-problems")
    public List<Map<String, Object>> fetchLsi(@RequestParam String dbName, @RequestParam String lsiId) throws Exception {
        OracleDbConfig config = configLoaderService.getConfigByName(dbName);
        if (config == null) throw new RuntimeException("Database config not found: " + dbName);

        String sql = queryXmlService.getQueryById("fetchLsi");
        if (sql == null) throw new RuntimeException("Query not found in XML for ID: fetchLsi");

        Map<String, Object> params = new HashMap<>();
        params.put("lsiId", lsiId);

        return oracleService.executeQuery(config, sql, params);
    }

    @GetMapping("/fetch-alarms")
    public List<Map<String, Object>> fetchHierarchy(@RequestParam String dbName, @RequestParam String lsiId) throws Exception {
        OracleDbConfig config = configLoaderService.getConfigByName(dbName);
        if (config == null) throw new RuntimeException("Database config not found: " + dbName);
        return hierarchyQueryService.getHierarchyResults(config, lsiId);
    }
/*    @GetMapping("/fetch-alarms")
    public List<Map<String, Object>> fetchAlarms(@RequestParam String dbName, @RequestParam String lsiId) throws Exception {
        OracleDbConfig config = configLoaderService.getConfigByName(dbName);
        if (config == null) throw new RuntimeException("Database config not found: " + dbName);

        String sql = queryXmlService.getQueryById("fetchAlarms");
        if (sql == null) throw new RuntimeException("Query not found in XML for ID: fetchAlarms");

        Map<String, Object> params = new HashMap<>();
        params.put("lsiId", lsiId);

        return oracleService.executeQuery(config, sql, params);
    }*/



}
