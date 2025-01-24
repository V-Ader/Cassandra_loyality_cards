package loyalty.db_operators;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.ExecutionInfo;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import com.datastax.oss.driver.api.core.cql.ResultSet;
import com.datastax.oss.driver.api.core.cql.Row;
import com.datastax.oss.driver.api.core.metadata.Node;
import com.datastax.oss.driver.api.core.session.Session;
import loyalty.database.config.CassandraConnectionConfig;

public class TokensTable {

    public static void createTokens(CqlSession session, CassandraConnectionConfig config, String issuer, String client, long tokens)  {
        String query = "UPDATE tokens_by_issuer_email_and_client_email SET tokens = tokens + ? WHERE issuer_email = ? AND client_email = ?";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(tokens, issuer, client);
        boundStatement.setConsistencyLevel(config.getWriteConsistency());
        session.execute(boundStatement);
    }

    public static long getTokenValue(CqlSession session, CassandraConnectionConfig config, String issuer, String client) {
        String query = "SELECT tokens FROM tokens_by_issuer_email_and_client_email WHERE issuer_email = ? AND client_email = ?";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(client, issuer);
        boundStatement.setConsistencyLevel(config.getReadConsistency());
        ResultSet resultSet = session.execute(boundStatement);

        long value = 0;

        for (Row row : resultSet) {
            value = row.getLong("tokens");
        }

        return value;
    }

    public static void useClientsToken(CqlSession session, CassandraConnectionConfig config, String issuer, String client, long value){
        String query = "UPDATE tokens_by_issuer_email_and_client_email SET tokens = tokens - ? WHERE issuer_email = ? AND client_email = ?;";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(value, issuer, client);
        boundStatement.setConsistencyLevel(config.getWriteConsistency());
//        session.execute(boundStatement);

        ResultSet resultSet = session.execute(boundStatement);

        // Retrieve the node that processed the query
        ExecutionInfo executionInfo = resultSet.getExecutionInfo();
        Node queriedNode = executionInfo.getCoordinator();

        // Print the node's address
        System.out.println("Query executed on node: " + queriedNode.getEndPoint());
    }
}
