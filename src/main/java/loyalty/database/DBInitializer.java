package loyalty.database;

import com.datastax.oss.driver.api.core.CqlSession;
import loyalty.database.config.CassandraConnectionConfig;
import loyalty.db_operators.CardByClientTable;
import loyalty.db_operators.CardByIssuerTable;
import loyalty.db_operators.KeySpace;
import loyalty.db_operators.LogsByIssuerTable;
import loyalty.db_operators.TokensTable;

public class DBInitializer {
    public static void initializationDB() {
        CassandraInitConnector initConnector = CassandraInitConnector.getInstance();
        CqlSession initSession;
        try {
            initSession = initConnector.getInitSession(0);
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }
        CassandraConnectionConfig config = CassandraConnectionConfig.getQUORUM();
        KeySpace.createKeySpace(initSession, config);
        KeySpace.useKeySpace(initSession, config);
        CardByClientTable.createCardByClientTable(initSession, config);
        CardByIssuerTable.createCardByIssuerTable(initSession, config);
        TokensTable.createTokensTable(initSession, config);
        LogsByIssuerTable.createLogsByIssuerTable(initSession, config);

        initSession.close();
    }
}
