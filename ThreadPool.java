package clubconnect.util;

import java.util.concurrent.*;

/**
 * Global thread pool for background tasks (notifications, backups, polling)
 */
public class ThreadPool {
    private static final ExecutorService exec = Executors.newFixedThreadPool(6);

    public static void run(Runnable r) {
        exec.submit(r);
    }

    public static void shutdown() {
        exec.shutdownNow();
    }
}
