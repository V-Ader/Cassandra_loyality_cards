package loyalty_benchamrk;

import com.datastax.oss.driver.api.core.CqlSession;
import loyalty.actors.client.ClientService;
import loyalty.actors.issuer.IssuerService;
import loyalty.actors.watcher.Watcher;
import loyalty.actors.watcher.WatcherInspectionResult;
import loyalty.database.config.CassandraConnectionConfig;
import loyalty.models.CardDTO;
import loyalty.models.CardId;

import java.util.ArrayList;
import java.util.List;


public class RealScenarioTest {

    List<CqlSession> sessions;

    int counter = 0;

    synchronized int getCounter(){
        counter += 1;
        return counter;
    }

    RealScenarioTest(List<CqlSession> sessions) {
        this.sessions = sessions;
    }

    BenchmarkResult runOnConsistencyLevel(CardId cardId, CassandraConnectionConfig config){
        int calls = 100;
        long tokens = calls / 2;
        int delay =  15; // in ms
        BenchmarkResult result = new BenchmarkResult();

        createCard(cardId, tokens);
        long startTime = System.nanoTime();
        ThreadRunner.runInThreads(() -> result.updateResults(useCard(cardId, config)), calls, delay);
        long endTime = System.nanoTime();

        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        WatcherInspectionResult inspectionResults = Watcher.inspect(getSession(), config).stream().filter(watcherInspectionResult -> watcherInspectionResult.getCardId().equals(cardId)).findFirst().orElse(new WatcherInspectionResult(cardId, 0, new ArrayList<>()));
        result.setAlerted(inspectionResults.getReportedLogs().size());
        result.setDuration(((endTime - startTime) / 1_000_000) - delay*calls); // in milis
        return result;
    }


    private void createCard(CardId cardId, long tokens) {
        IssuerService service = new IssuerService(cardId.getIssuerEmail(), getSession(), CassandraConnectionConfig.getDefault());
        service.createCard(cardId.getClientEmail(), tokens);
    }

    private boolean useCard(CardId cardId, CassandraConnectionConfig config) {
        ClientService service = new ClientService(cardId.getClientEmail(), getSession(), config);
        return service.useClientsToken(cardId.getIssuerEmail(), 1);
    }

    private CqlSession getSession(){
        return sessions.get(getCounter() % this.sessions.size());
    }
}
