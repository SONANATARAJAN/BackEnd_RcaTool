package GeneratedAlarms.Alarms.Service;
import GeneratedAlarms.Alarms.Model.OracleDbConfig;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.stereotype.Service;
import java.sql.*;
import java.util.*;

@Service
public class OracleService {

    public List<Map<String, Object>> executeQuery(OracleDbConfig config, String sql, Map<String, Object> params) throws Exception {
        String jdbcUrl = String.format("jdbc:oracle:thin:@%s:%s:%s",
                config.getHost(), config.getPort(), config.getSid());

        OracleDataSource dataSource = new OracleDataSource();
        dataSource.setURL(jdbcUrl);
        dataSource.setUser(config.getUsername());
        dataSource.setPassword(config.getPassword());

        List<Map<String, Object>> results = new ArrayList<>();

        // Replace :param with ? for PreparedStatement
        String parsedSql = sql.replaceAll(":[a-zA-Z0-9_]+", "?");

        try (Connection conn = dataSource.getConnection();
             PreparedStatement stmt = conn.prepareStatement(parsedSql)) {

            // Bind parameters in order
            int index = 1;
            for (String key : getParameterOrder(sql)) {
                stmt.setObject(index++, params.get(key));
            }

            ResultSet rs = stmt.executeQuery();
            ResultSetMetaData meta = rs.getMetaData();
            int columnCount = meta.getColumnCount();

            while (rs.next()) {
                Map<String, Object> row = new LinkedHashMap<>();
                for (int i = 1; i <= columnCount; i++) {
                    row.put(meta.getColumnName(i), rs.getObject(i));
                }
                results.add(row);
            }
        }
        return results;
    }

    private List<String> getParameterOrder(String sql) {
        List<String> params = new ArrayList<>();
        int index = 0;
        while ((index = sql.indexOf(":", index)) != -1) {
            int end = index + 1;
            while (end < sql.length() && Character.isLetterOrDigit(sql.charAt(end))) end++;
            params.add(sql.substring(index + 1, end));
            index = end;
        }
        return params;
    }
}
