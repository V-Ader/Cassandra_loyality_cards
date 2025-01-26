package loyalty.db_operators;

import com.datastax.oss.driver.api.core.CqlSession;
import com.datastax.oss.driver.api.core.cql.BoundStatement;
import com.datastax.oss.driver.api.core.cql.PreparedStatement;
import loyalty.database.config.CassandraConnectionConfig;

public class KeySpace {
    public static void createKeySpace(CqlSession session, CassandraConnectionConfig config) {
        String query = "CREATE KEYSPACE IF NOT EXISTS loyalty WITH replication = {'class': 'SimpleStrategy', 'replication_factor': 2};";
        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind();
        boundStatement = boundStatement.setConsistencyLevel(config.getWriteConsistency());
        session.execute(boundStatement);
        System.out.println("Keyspace loyalty was created successfully.");

    }

    public static void useKeySpace(CqlSession session, CassandraConnectionConfig config) {
        String query = "USE loyalty;";
        PreparedStatement preparedStatement = session.prepare(query);
        BoundStatement boundStatement = preparedStatement.bind();
        boundStatement = boundStatement.setConsistencyLevel(config.getWriteConsistency());
        session.execute(boundStatement);
    }
}
