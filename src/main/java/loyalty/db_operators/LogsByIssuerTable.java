package loyalty.db_operators;


import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.DefaultConsistencyLevel;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import loyalty.database.config.CassandraConnectionConfig;
import loyalty.models.CardId;
import loyalty.models.Log;

import java.time.Instant;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class LogsByIssuerTable {

    public static void createLogsByIssuerTable(CqlSession session, CassandraConnectionConfig config) {
        String query = "CREATE TABLE IF NOT EXISTS logs_by_issuer_email_and_client_email (\n" +
                "    issuer_email TEXT,\n" +
                "    client_email TEXT,\n" +
                "    change_timestamp TIMESTAMP,\n" +
                "    previous_value INT,\n" +
                "    new_value INT,\n" +
                "    PRIMARY KEY ((client_email, issuer_email), change_timestamp)\n" +
                ");";
        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind();
        boundStatement = boundStatement.setConsistencyLevel(config.getWriteConsistency());
        session.execute(boundStatement);
        System.out.println("LogsByIssuerTable was created successfully.");

    }

    public static List<Log> getLogs(CqlSession session, CassandraConnectionConfig config, String issuerEmail, String clientEmail) {
        String query = "SELECT issuer_email, client_email, change_timestamp, previous_value, new_value " +
                "FROM logs_by_issuer_email_and_client_email " +
                "WHERE issuer_email = ? AND client_email = ?";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(issuerEmail, clientEmail);
        boundStatement = boundStatement.setConsistencyLevel(config.getReadConsistency());
        ResultSet resultSet = session.execute(boundStatement);

        List<Log> logs = new LinkedList<>();

        for (Row row : resultSet) {
            Log logDTO = Log.builder()
                    .issuerEmail(row.getString("issuer_email"))
                    .clientEmail(row.getString("client_email"))
                    .changeTimestamp(row.getInstant("change_timestamp"))
                    .previousValue(row.getInt("previous_value"))
                    .newValue(row.getInt("new_value"))
                    .build();
            logs.add(logDTO);
        }
        return logs;
    }

    public static Set<CardId> getAllLoggedCards(CqlSession session, CassandraConnectionConfig config) {
        String query = "SELECT issuer_email, client_email, change_timestamp, previous_value, new_value " +
                "FROM logs_by_issuer_email_and_client_email";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind();
        boundStatement = boundStatement.setConsistencyLevel(config.getReadConsistency());
        ResultSet resultSet = session.execute(boundStatement);

        Set<CardId> cards = new HashSet<>();
        for (Row row : resultSet) {
            cards.add(new CardId(row.getString("issuer_email"), row.getString("client_email")));
        }
        return cards;
    }

    public static void addLog(CqlSession session, CassandraConnectionConfig config, String issuerEmail, String clientEmail, Instant changeTimestamp, long previousValue, long newValue) {
        String query = "INSERT INTO logs_by_issuer_email_and_client_email " +
                "(issuer_email, client_email, change_timestamp, previous_value, new_value) " +
                "VALUES (?, ?, ?, ?, ?) USING TTL 300"; // 5 minutes

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(issuerEmail, clientEmail, changeTimestamp, previousValue, newValue);
        boundStatement = boundStatement.setConsistencyLevel(config.getWriteConsistency());
        session.execute(boundStatement);
    }

    public static List<Log> getLogsByTotalChange(CqlSession session, String issuer, String client, long totalChange) {
        String query = "SELECT issuer_email, client_email, change_timestamp, previous_value, new_value " +
                "FROM logs_by_issuer_email_and_client_email WHERE issuer_email = ? AND client_email = ?";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(issuer, client);
        boundStatement = boundStatement.setConsistencyLevel(DefaultConsistencyLevel.ALL);
        ResultSet resultSet = session.execute(boundStatement);

        List<Log> matchingLogs = new LinkedList<>();

        for (Row row : resultSet) {
            int previousValue = row.getInt("previous_value");
            int newValue = row.getInt("new_value");
            if (Math.abs(newValue - previousValue) == totalChange) {
                Log logDTO = Log.builder()
                        .issuerEmail(row.getString("issuer_email"))
                        .clientEmail(row.getString("client_email"))
                        .changeTimestamp(row.getInstant("change_timestamp"))
                        .previousValue(previousValue)
                        .newValue(newValue)
                        .build();
                matchingLogs.add(logDTO);
            }
        }

        return matchingLogs;
    }
}
