package loyalty_benchamrk;

import com.datastax.oss.driver.api.core.CqlSession;
import loyalty.actors.client.ClientService;
import loyalty.actors.issuer.IssuerService;
import loyalty.database.config.CassandraConnectionConfig;
import loyalty.models.CardDTO;


public class ExampleTest {

    CqlSession session;

    ExampleTest(CqlSession session) {
        this.session = session;
    }

    boolean run(String issuer, String client) {
        int calls = 10;
        long tokens = 10;
        long expectedValue = 0;

        createCard(client, issuer, tokens);
        ThreadRunner.runInThreads(() -> useCard(client, issuer), calls);
        CardDTO card = getCard(client, issuer);

        return card.getTokens() == expectedValue;
    }


    private CardDTO getCard(String client, String issuer) {
        IssuerService service = new IssuerService(issuer, session, CassandraConnectionConfig.getConsistencyOne());
        return service.getCard(client);
    }

    private void createCard(String client, String issuer, long tokens) {
        IssuerService service = new IssuerService(issuer, session, CassandraConnectionConfig.getDefault());
        service.createCard(client,tokens);
    }

    private void useCard(String client, String issuer) {
        ClientService service = new ClientService(client, session, CassandraConnectionConfig.getConsistencyOne());
        service.useClientsToken(issuer, 1);
    }
}
