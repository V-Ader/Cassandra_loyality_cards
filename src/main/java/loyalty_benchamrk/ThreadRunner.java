package loyalty_benchamrk;

import java.util.ArrayList;
import java.util.List;

public class ThreadRunner {
    public static void runInThreads(Runnable task, int numberOfCalls, int delay) {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < numberOfCalls; i++) {
            try {
                Thread thread = new Thread(task);
                threads.add(thread);
                thread.start();
                Thread.sleep(delay);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }

        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }
    }
}
