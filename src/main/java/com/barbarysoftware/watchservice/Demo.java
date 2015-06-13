package com.barbarysoftware.watchservice;

import static com.barbarysoftware.watchservice.StandardWatchEventKind.*;

import java.io.File;
import java.io.IOException;

public class Demo {

    public static void main(String[] args) throws IOException, InterruptedException {

        final WatchService watcher = WatchService.newWatchService();

        final String home = System.getProperty("user.home");
        final WatchableFile file1 = new WatchableFile(new File(home + "/Downloads"));
        final WatchableFile file2 = new WatchableFile(new File(home + "/Documents"));

        file1.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        file2.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

        final Thread consumer = new Thread(createRunnable(watcher));
        consumer.start();
        System.out.printf("Watching for changes in %s and %s for 1 minute ...%n", file1.getFile(), file2.getFile());
        Thread.sleep(60 * 1000);
        consumer.interrupt();
        watcher.close();

    }

    private static Runnable createRunnable(final WatchService watcher) {
        return new Runnable() {
            public void run() {
                for (; ;) {

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
                        System.out.printf("Detected file system event: %s at %s%n", kind, ev.context());

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
