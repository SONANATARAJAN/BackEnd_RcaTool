package GeneratedAlarms.Alarms.Service;

import GeneratedAlarms.Alarms.Config.OracleDbConfigLoader;
import GeneratedAlarms.Alarms.Model.OracleDbConfig;
import org.springframework.stereotype.Service;
import java.util.Map;

@Service
public class ConfigLoaderService {

    private final Map<String, OracleDbConfig> configMap;

    public ConfigLoaderService() throws Exception {
        OracleDbConfigLoader loader = new OracleDbConfigLoader();
        this.configMap = loader.loadConfigs();
    }

    public OracleDbConfig getConfigByName(String name) {
        return configMap.get(name);
    }

    public Map<String, OracleDbConfig> getAllConfigs() {
        return configMap;
    }
}
