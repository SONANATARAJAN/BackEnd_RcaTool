package GeneratedAlarms.Alarms.Service;

import GeneratedAlarms.Alarms.Model.OracleDbConfig;
import oracle.jdbc.pool.OracleDataSource;
import org.springframework.stereotype.Service;

import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class HierarchyQueryService {

    public List<Map<String, Object>> getHierarchyResults(OracleDbConfig config, String objectName) throws Exception {
        OracleDataSource dataSource = new OracleDataSource();
        dataSource.setUser(config.getUsername());
        dataSource.setPassword(config.getPassword());
        dataSource.setURL(String.format("jdbc:oracle:thin:@%s:%s:%s",
                config.getHost(), config.getPort(), config.getSid()));
        Connection conn = dataSource.getConnection();

        // Step 1: Get list of table names
        Statement stmt = conn.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT table_name FROM user_tables WHERE table_name LIKE 'RCA_HIER_HIS_RCI_%'");

        List<String> tableNames = new ArrayList<>();
        while (rs.next()) {
            tableNames.add(rs.getString("table_name"));
        }

        // Step 2: Filter tables by date (last 30 days)
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(1);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd_M_uuuu");

        List<String> filteredTables = tableNames.stream().filter(name -> {
            try {
                String[] parts = name.split("RCA_HIER_HIS_RCI_");
                if (parts.length > 1) {
                    LocalDate tableDate = LocalDate.parse(parts[1], formatter);
                    return !tableDate.isBefore(thirtyDaysAgo);
                }
                return false;
            } catch (Exception e) {
                return false;
            }
        }).collect(Collectors.toList());

        if (filteredTables.isEmpty()) return List.of();

        // Step 3: Get child ID
        PreparedStatement ps = conn.prepareStatement("SELECT id FROM rca_rci WHERE objectname = ?");
        ps.setString(1, objectName);
        ResultSet childRs = ps.executeQuery();
        Long childId = null;
        if (childRs.next()) childId = childRs.getLong("id");
        if (childId == null) return List.of();

        // Step 4: Construct UNION query
        StringBuilder unionQuery = new StringBuilder();
        for (String table : filteredTables) {
            unionQuery.append("SELECT parentid, childid FROM ").append(table).append(" UNION ALL ");
        }
        unionQuery.setLength(unionQuery.length() - " UNION ALL ".length());

        // Step 5: Final SQL
        String finalQuery = String.format("""
                SELECT id as rciid, objecttype, eventtypespecificuniquekey, referobjtype, objectid, objectname, additionalinfo,
                       to_date('01/01/1970 05:30:00','DD/MM/YYYY HH24:MI:SS') + (firsteventcomputedtime /1000/60/60/24) as firsteventcomputedtime,
                       to_date('01/01/1970 05:30:00','DD/MM/YYYY HH24:MI:SS') + (recenteventcomputedtime /1000/60/60/24) as recenteventcomputedtime
                FROM rca_rci 
                WHERE id IN (
                    SELECT parentid FROM (
                        WITH combined_hierarchy AS (
                            %s
                        )
                        SELECT parentid
                        FROM combined_hierarchy
                        START WITH childid = %d
                        CONNECT BY PRIOR parentid = childid
                    )
                )
                """, unionQuery.toString(), childId);

        // Step 6: Execute final query
        Statement finalStmt = conn.createStatement();
        ResultSet resultSet = finalStmt.executeQuery(finalQuery);

        ResultSetMetaData metaData = resultSet.getMetaData();
        int columnCount = metaData.getColumnCount();
        List<Map<String, Object>> results = new ArrayList<>();

        while (resultSet.next()) {
            Map<String, Object> row = new LinkedHashMap<>();
            for (int i = 1; i <= columnCount; i++) {
                row.put(metaData.getColumnLabel(i), resultSet.getObject(i));
            }
            results.add(row);
        }

        conn.close();
        return results;
    }
}