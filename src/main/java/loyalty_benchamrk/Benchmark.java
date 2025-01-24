package loyalty_benchamrk;

import com.datastax.oss.driver.api.core.CqlSession;
import loyalty.database.ConnectionException;
import loyalty.database.connector.CassandraCommonConnector;

import java.util.ArrayList;
import java.util.List;

public class Benchmark {
    public static void main(String[] args) throws InterruptedException {
        List<String> issuers = EmailGenerator.getEmails("issuer-10", 5);
        List<String> clients = EmailGenerator.getEmails("client-10", 5);

        List<Thread> threads = new ArrayList<>();
        CqlSession session = getSession();


        for (String issuer : issuers) {
            for (String client : clients) {
                Thread thread = new Thread(() -> {
                    ExampleTest test = new ExampleTest(session);
                    if(test.run(issuer, client)) {
                        System.out.println("PASSED");
                    } else {
                        System.out.println("FAILED");
                    }
                });
                threads.add(thread);
                thread.start();
            }
        }

        for (Thread thread : threads) {
            thread.join();
        }
        session.close();
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


}

