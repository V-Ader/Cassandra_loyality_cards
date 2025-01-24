package loyalty_benchamrk;

import com.datastax.driver.core.ConsistencyLevel;
import loyalty.actors.client.ClientSession;
import loyalty.actors.issuer.IssuerSession;
import loyalty.database.ConnectionException;
import loyalty.database.config.CassandraConnectionConfig;
import loyalty.models.CardDTO;


public class ExampleTest {
    static boolean run(String issuer, String client) {
        int calls = 10;
        long tokens = 10;
        long expectedValue = 0;

        createCard(client, issuer, tokens);
        ThreadRunner.runInThreads(() -> useCard(client, issuer), calls);
        CardDTO card = getCard(client, issuer);

        return card.getTokens() == expectedValue;
    }

    private static IssuerSession createIssuerSession(String issuer) {
        try {
            return new IssuerSession(issuer, CassandraConnectionConfig.getConsistencyOne());
        } catch (ConnectionException e) {
            throw new RuntimeException("Failed to create issuer session for: " + issuer, e);
        }
    }

    private static CardDTO getCard(String client, String issuer) {
        IssuerSession issuerSession = createIssuerSession(issuer);

        CardDTO card = issuerSession.getCard(client);
        issuerSession.closeConnection();
        return card;
    }

    private static void createCard(String client, String issuer, long tokens) {
        IssuerSession issuerSession = createIssuerSession(issuer);
        issuerSession.createCard(client,tokens);
        issuerSession.closeConnection();
    }

    private static void useCard(String client, String issuer) {
        ClientSession session;
        try {
            session = new ClientSession(
                    client,
                    issuer);
        } catch (ConnectionException e) {
            throw new RuntimeException("Failed to consume token for client: " + client, e);
        }
        session.useToken();
        session.closeConnection();
    }

    private ExampleTest() {}
}
