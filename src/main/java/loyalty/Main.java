package loyalty;

import com.datastax.oss.driver.api.core.CqlSession;
import loyalty.actors.client.ClientService;
import loyalty.database.CassandraInitConnector;
import loyalty.database.ConnectionException;
import loyalty.database.config.CassandraConnectionConfig;
import loyalty.database.connector.CassandraCommonConnector;
import loyalty.db_operators.CardByClientTable;
import loyalty.db_operators.CardByIssuerTable;
import loyalty.db_operators.KeySpace;
import loyalty.db_operators.LogsByIssuerTable;
import loyalty.db_operators.TokensTable;
import loyalty.models.CardDTO;

import java.security.Key;



public class Main {
    public static void main(String[] args) {
        String clientEmail = "client1@example.com";
        String issuerEmail = "issuer1@example.com";

        CassandraConnectionConfig config = CassandraConnectionConfig.builder().build();
        CassandraInitConnector initConnector = CassandraInitConnector.getInstance();
        CqlSession initSession;
        try {
            initSession = initConnector.getInitSession(0);
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }

        InitializationDB(initSession, config);
        initSession.close();

        CassandraCommonConnector connector = CassandraCommonConnector.getInstance();
        CqlSession session;
        try {
            session = connector.connect();
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }

        ClientService service = new ClientService(clientEmail, session);
        for(CardDTO card : service.selectClientsCards()) {
            System.out.println(card.toString());
        }

        session.close();
        return;
    }

    public static void InitializationDB(CqlSession initSession, CassandraConnectionConfig config) {
        KeySpace.createKeySpace(initSession, config);
        KeySpace.useKeySpace(initSession, config);
        CardByClientTable.createCardByClientTable(initSession, config);
        CardByIssuerTable.createCardByIssuerTable(initSession, config);
        TokensTable.createTokensTable(initSession, config);
        LogsByIssuerTable.createLogsByIssuerTable(initSession, config);
    }
}
