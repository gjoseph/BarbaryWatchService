package com.barbarysoftware.watchservice;

import junit.framework.TestCase;

import java.io.File;

import static com.barbarysoftware.watchservice.StandardWatchEventKind.*;

public class IntegrationTest extends TestCase {

    public void testItDoesSomethingInAThread() throws Exception {
        System.out.println("okay");

        final WatchService watcher = WatchService.newWatchService();

        final String home = System.getProperty("user.home");
        final WatchableFile file1 = new WatchableFile(new File(home + "/Downloads"));
        final WatchableFile file2 = new WatchableFile(new File(home + "/Documents"));

        file1.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        file2.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

        Runnable runnable = createRunnable(watcher);
        final Thread consumer = new Thread(runnable);
        consumer.start();
        System.out.println("Watching for changes for 1 minute...");
        Thread.sleep(60000);
        consumer.interrupt();
        watcher.close();

    }

    private static Runnable createRunnable(final WatchService watcher) {
        return new Runnable() {
            public void run() {
                for (; ; ) {

                    // wait for key to be signaled
                    WatchKey key;
                    try {
                        key = watcher.take();
                    } catch (InterruptedException x) {
                        return;
                    }
                    for (WatchEvent<?> event : key.pollEvents()) {
                        WatchEvent.Kind<?> kind = event.kind();

                        if (kind == OVERFLOW) {
                            continue;
                        }
                        // The filename is the context of the event.
                        @SuppressWarnings({"unchecked"}) WatchEvent<File> ev = (WatchEvent<File>) event;
                        System.out.println("detected file system event: " + ev.context() + " " + kind);

                    }

                    // Reset the key -- this step is critical to receive further watch events.

                    boolean valid = key.reset();
                    if (!valid) {
                        break;
                    }

                }
            }
        };
    }

}