package GeneratedAlarms.Alarms.Model;

import jakarta.xml.bind.annotation.*;
import java.util.List;

@XmlRootElement(name = "queries")
@XmlAccessorType(XmlAccessType.FIELD)
public class QueryXmlModel {

    @XmlElement(name = "query")
    private List<QueryEntry> queries;

    public List<QueryEntry> getQueries() {
        return queries;
    }

    @XmlAccessorType(XmlAccessType.FIELD)
    public static class QueryEntry {

        @XmlAttribute(name = "id")
        private String id;

        @XmlValue
        private String query;

        public String getId() {
            return id;
        }

        public String getQuery() {
            return query.trim();
        }
    }
}
