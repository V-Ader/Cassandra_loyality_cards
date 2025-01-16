package loyalty_benchamrk;

import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        List<String> issuers = EmailGenerator.getEmails("issuer-multiple-2", 2);
        List<String> clients = EmailGenerator.getEmails("client-multiple-2", 2);

        List<Thread> threads = new ArrayList<>();


        for (String issuer : issuers) {
            for (String client : clients) {
                Thread thread = new Thread(() -> tryToRun(issuer, client));
                threads.add(thread);
                thread.start();
            }
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }

    private static void tryToRun(String issuer, String client){
        try {
            ExampleTest.run(issuer, client);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }
}
