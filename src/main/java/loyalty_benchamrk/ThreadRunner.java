package loyalty_benchamrk;

import java.util.ArrayList;
import java.util.List;

public class ThreadRunner {
    public static void runInThreads(Runnable task, int numberOfCalls) throws InterruptedException {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numberOfCalls; i++) {
            Thread thread = new Thread(task);
            threads.add(thread);
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }
}
