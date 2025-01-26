package loyalty.db_operators;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.ExecutionInfo;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.metadata.Node;
import loyalty.actors.issuer.IssuerService;
import loyalty.database.config.CassandraConnectionConfig;
import loyalty.models.Token;

import java.time.Instant;

public class TokensTable {

    public static void createTokensTable(CqlSession session, CassandraConnectionConfig config) {
        String query = "CREATE TABLE IF NOT EXISTS tokens_by_issuer_email_and_client_email (issuer_email TEXT, client_email TEXT, tokens COUNTER, PRIMARY KEY ((client_email, issuer_email)));";
        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind();
        boundStatement = boundStatement.setConsistencyLevel(config.getWriteConsistency());
        session.execute(boundStatement);
        System.out.println("TokensTable was created successfully.");

    }

    public static void createTokens(CqlSession session, CassandraConnectionConfig config, String issuer, String client, long tokens)  {
        String query = "UPDATE tokens_by_issuer_email_and_client_email SET tokens = tokens + ? WHERE issuer_email = ? AND client_email = ?";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(tokens, issuer, client);
        boundStatement = boundStatement.setConsistencyLevel(config.getWriteConsistency());
        session.execute(boundStatement);
    }

    public static long getTokenValue(CqlSession session, CassandraConnectionConfig config, String issuer, String client) {
        String query = "SELECT tokens FROM tokens_by_issuer_email_and_client_email WHERE issuer_email = ? AND client_email = ?";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(issuer, client);
        boundStatement = boundStatement.setConsistencyLevel(config.getReadConsistency());
        ResultSet resultSet = session.execute(boundStatement);
        // Retrieve the node that processed the query
        ExecutionInfo executionInfo = resultSet.getExecutionInfo();
        Node queriedNode = executionInfo.getCoordinator();
        assert queriedNode != null;

        long value = 0;

        for (Row row : resultSet) {
            value = row.getLong("tokens");
        }

        return value;
    }

    public static boolean useClientsToken(CqlSession session, CassandraConnectionConfig config, String issuer, String client, long value){
        if ("INVALID".equals(CardByIssuerTable.getCard(session, config, issuer, client).getStatus())) {
            return false;
        }
        long current = getTokenValue(session, config, issuer, client);
        if ( current < value) {
            return false;
        }
        reduceTokens(session, config, issuer, client, value);
        LogsByIssuerTable.addLog(session, config, issuer, client, Instant.now(), current, current + value);

//        current = getTokenValue(session, config, issuer, client);
//        if ( current < 0) {
//            reduceTokens(session, config, issuer, client, -value);
//            return false;
//        }

        return true;
    }

    public static void reduceTokens(CqlSession session, CassandraConnectionConfig config, String issuer, String client, long value){
        String query = "UPDATE tokens_by_issuer_email_and_client_email SET tokens = tokens - ? WHERE issuer_email = ? AND client_email = ?;";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(value, issuer, client);
        boundStatement = boundStatement.setConsistencyLevel(config.getWriteConsistency());

        ResultSet resultSet = session.execute(boundStatement);
        resultSet.getExecutionInfo();
    }
}
