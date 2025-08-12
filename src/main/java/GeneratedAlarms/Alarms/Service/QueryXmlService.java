package GeneratedAlarms.Alarms.Service;
import GeneratedAlarms.Alarms.Model.QueryXmlModel;
import jakarta.annotation.PostConstruct;
import jakarta.xml.bind.JAXBContext;
import jakarta.xml.bind.Unmarshaller;
import org.springframework.stereotype.Service;
import java.io.InputStream;
import java.util.HashMap;
 import java.util.Map;

@Service
public class QueryXmlService {

    private final Map<String, String> queries = new HashMap<>();

    @PostConstruct
    public void loadQueries() {
        try {
            InputStream is = getClass().getClassLoader().getResourceAsStream("queries.xml");
            JAXBContext context = JAXBContext.newInstance(QueryXmlModel.class);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            QueryXmlModel queryList = (QueryXmlModel) unmarshaller.unmarshal(is);

            for (QueryXmlModel.QueryEntry entry : queryList.getQueries()) {
                queries.put(entry.getId(), entry.getQuery());
            }
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse XML queries", e);
        }
    }

    public String getQueryById(String id) {
        return queries.get(id);
    }
}
