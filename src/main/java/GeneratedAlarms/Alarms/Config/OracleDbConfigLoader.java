package GeneratedAlarms.Alarms.Config;

import GeneratedAlarms.Alarms.Model.OracleDbConfig;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OracleDbConfigLoader {

    public Map<String, OracleDbConfig> loadConfigs() throws Exception {
        Map<String, OracleDbConfig> configMap = new HashMap<>();

        InputStream is = getClass().getClassLoader().getResourceAsStream("db-config.xml");
        if (is == null) throw new RuntimeException("db-config.xml not found");

        Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(is);
        doc.getDocumentElement().normalize();

        NodeList dbList = doc.getElementsByTagName("configs");

        for (int i = 0; i < dbList.getLength(); i++) {
            Node node = dbList.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE) {
                Element e = (Element) node;
                OracleDbConfig config = new OracleDbConfig();
                config.setId(getTagValue("id", e));
                config.setName(getTagValue("name", e));
                config.setHost(getTagValue("host", e));
                config.setPort(getTagValue("port", e));
                config.setSid(getTagValue("sid", e));
                config.setUsername(getTagValue("username", e));
                config.setPassword(getTagValue("password", e));

                configMap.put(config.getName(), config);
                System.out.println("Loaded config: " + config.getName());
            }
        }
        return configMap;
    }



    private String getTagValue(String tag, Element element) {
        NodeList nl = element.getElementsByTagName(tag);
        if (nl.getLength() > 0) return nl.item(0).getTextContent();
        return null;
    }
}
