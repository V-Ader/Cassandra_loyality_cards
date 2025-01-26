package loyalty_benchamrk;

import com.datastax.oss.driver.api.core.CqlSession;
import loyalty.actors.client.ClientService;
import loyalty.actors.issuer.IssuerService;
import loyalty.database.config.CassandraConnectionConfig;
import loyalty.models.CardDTO;

import java.util.List;


public class ExampleTest {

    List<CqlSession> sessions;

    int counter = 0;

    synchronized int getCounter(){
        counter += 1;
        return counter;
    }

    ExampleTest(List<CqlSession> sessions) {
        this.sessions = sessions;
    }

    BenchmarkResult run(String issuer, String client) {
        int calls = 10;
        long tokens = 5;

        BenchmarkResult result = new BenchmarkResult();

        createCard(client, issuer, tokens);
        long startTime = System.nanoTime();
        ThreadRunner.runInThreads(() -> result.updateResults(useCard(client, issuer)), calls);
        long endTime = System.nanoTime();
        CardDTO card = getCard(client, issuer);
        System.out.println(card.getTokens());
        result.setDuration((endTime - startTime) / 1_000_000); // in milis
        return result;
    }


    private CardDTO getCard(String client, String issuer) {
        IssuerService service = new IssuerService(issuer, getSession(), CassandraConnectionConfig.getDefault());
        return service.getCard(client);
    }

    private void createCard(String client, String issuer, long tokens) {
        IssuerService service = new IssuerService(issuer, getSession(), CassandraConnectionConfig.getDefault());
        service.createCard(client,tokens);
    }

    private boolean useCard(String client, String issuer) {
//        try{
//            Thread.sleep(100);
//        } catch (InterruptedException ignored) {}
        ClientService service = new ClientService(client, getSession(), CassandraConnectionConfig.getDefault());
        return service.useClientsToken(issuer, 1);
    }

    private CqlSession getSession(){
        return sessions.get(getCounter() % this.sessions.size());
    }
}
