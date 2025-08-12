package GeneratedAlarms.Alarms.Controller;
import GeneratedAlarms.Alarms.Model.ServerConfigPatch;
import GeneratedAlarms.Alarms.Service.ServerConfigService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servers")
public class ServerConfigController {

    private final ServerConfigService service;

    public ServerConfigController(ServerConfigService service) {
        this.service = service;
    }

    @GetMapping
    public List<ServerConfigPatch> getAllServers() {
        return service.getAllServers();
    }

    @GetMapping("/{id}")
    public ServerConfigPatch getServerById(@PathVariable int id) {
        return service.getServerById(id).orElseThrow(() -> new RuntimeException("Server not found"));
    }
}
