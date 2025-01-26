package loyalty.db_operators;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.ExecutionInfo;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.metadata.Node;
import loyalty.database.config.CassandraConnectionConfig;

public class TokensTable {

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
//        System.out.println("Query 'getTokenValue' executed on node: " + queriedNode.getEndPoint());


        long value = 0;

        for (Row row : resultSet) {
            value = row.getLong("tokens");
        }

        return value;
    }

    public static boolean useClientsToken(CqlSession session, CassandraConnectionConfig config, String issuer, String client, long value){
        long current = getTokenValue(session, config, issuer, client);
        if ( current < value) {
            return false;
        }
        reduceTokens(session, config, issuer, client, value);

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

        // Retrieve the node that processed the query
        ExecutionInfo executionInfo = resultSet.getExecutionInfo();
        Node queriedNode = executionInfo.getCoordinator();

        // Print the node's address
        assert queriedNode != null;
    }
}
