package loyalty_benchamrk;

import com.datastax.oss.driver.api.core.CqlSession;
import loyalty.database.ConnectionException;
import loyalty.database.DBInitializer;
import loyalty.database.config.CassandraConnectionConfig;
import loyalty.database.connector.CassandraCommonConnector;
import loyalty.models.CardId;

import java.util.ArrayList;
import java.util.List;

public class Benchmark {
    public static void main(String[] args) throws InterruptedException {

        DBInitializer.initializationDB();

        List<String> issuers = EmailGenerator.getEmails("issuer-80020", 5);
        List<String> clients = EmailGenerator.getEmails("client-80020", 4);

        List<Thread> threads = new ArrayList<>();
        BenchmarkResult finalResult = new BenchmarkResult();
        List<CqlSession> sessions = new ArrayList<>();
        sessions.add(getSession(0));
        sessions.add(getSession(1));

        for (String issuer : issuers) {
            for (String client : clients) {
                Thread thread = new Thread(() -> {
                    RealScenarioTest test = new RealScenarioTest(sessions);
                    finalResult.merge(test.runOnConsistencyLevel(new CardId(issuer, client), CassandraConnectionConfig.getConsistencyOne()));
                });
                threads.add(thread);
                thread.start();
            }
        }

        for (Thread thread : threads) {
            thread.join();
        }

        for (CqlSession session : sessions) {
            session.close();
        }

        if (finalResult.accepted - finalResult.alerted == finalResult.executions / 2 ) {
            System.out.println("TEST PASSED");
        } else {
            System.out.println("TEST FAILED");
        }
        System.out.println(finalResult);
        System.out.printf("Avg duration: %d", finalResult.duration / finalResult.executions);
    }


    private static CqlSession getSession() {
        CassandraCommonConnector connector = CassandraCommonConnector.getInstance();
        CqlSession session;
        try {
            session = connector.connect();
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }
        return session;
    }

    private static CqlSession getSession(int id) {
        CassandraCommonConnector connector = CassandraCommonConnector.getInstance();
        CqlSession session;
        try {
            session = connector.connect(id);
        } catch (ConnectionException e) {
            throw new RuntimeException(e);
        }
        return session;
    }
}

