package clubconnect.service;

import clubconnect.util.ThreadPool;

import java.util.List;

/**
 * Simple notification service that simulates sending emails/messages in background threads.
 */
public class NotificationService {
    public interface SendCallback { void onSent(String info); }
    public void sendBulk(List<String> recipients, String subject, String message, SendCallback cb) {
        ThreadPool.run(() -> {
            for (String r : recipients) {
                // simulate sending (could integrate JavaMail)
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
                if (cb != null) cb.onSent("Sent to " + r);
            }
        });
    }
}
