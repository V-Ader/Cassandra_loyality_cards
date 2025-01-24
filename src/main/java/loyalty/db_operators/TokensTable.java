package loyalty.db_operators;

import com.datastax.driver.core.*;
import loyalty.database.config.CassandraConnectionConfig;

public class TokensTable {

    public static void createTokens(Session session, CassandraConnectionConfig config, String issuer, String client, long tokens)  {
        String query = "UPDATE tokens_by_issuer_email_and_client_email SET tokens = tokens + ? WHERE issuer_email = ? AND client_email = ?";

        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind(tokens, issuer, client);
        boundStatement.setConsistencyLevel(config.getWriteConsistency());
        session.execute(boundStatement);
    }

    public static long getTokenValue(Session session, CassandraConnectionConfig config, String issuer, String client) {
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

    public static void useClientsToken(Session session, CassandraConnectionConfig config, String issuer, String client, long value){
        String query = "UPDATE tokens_by_issuer_email_and_client_email SET tokens = tokens - ? WHERE issuer_email = ? AND client_email = ?;";

        PreparedStatement preparedStatement = session.prepare(query).setConsistencyLevel(config.getWriteConsistency());
        BoundStatement boundStatement = preparedStatement.bind(value, issuer, client);
        session.execute(boundStatement);
    }
}
