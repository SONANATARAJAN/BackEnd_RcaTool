package GeneratedAlarms.Alarms.Service;


import GeneratedAlarms.Alarms.Model.ServerConfigPatch;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;
import java.util.Optional;

@Service
public class ServerConfigService {

    private List<ServerConfigPatch> servers;

    @PostConstruct
    public void loadConfigs() {
        try {
            ObjectMapper mapper = new ObjectMapper();
            InputStream is = getClass().getResourceAsStream("/server-config.json");
            servers = mapper.readValue(is, new TypeReference<List<ServerConfigPatch>>() {
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public List<ServerConfigPatch> getAllServers() {
        return servers;
    }

    public Optional<ServerConfigPatch> getServerById(int id) {
        return servers.stream().filter(s -> s.getId() == id).findFirst();
    }
}
