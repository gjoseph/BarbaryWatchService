package com.barbarysoftware.watchservice;

import java.io.File;
import java.io.IOException;
import java.nio.file.WatchEvent;
import java.nio.file.WatchKey;
import java.nio.file.WatchService;

import static java.nio.file.StandardWatchEventKinds.*;

public class Demo {

    public static void main(String[] args) throws IOException, InterruptedException {

        final WatchService watcher = WatchServiceFactory.newWatchService();

        final String home = System.getProperty("user.home");
        final WatchableFile file1 = new WatchableFile(new File(home + "/Downloads"));
        final WatchableFile file2 = new WatchableFile(new File(home + "/Documents"));
        final WatchableFile file3 = new WatchableFile(new File(home + "/Leoz/transfer/out/bf986ce3"));

        file1.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        file2.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);
        file3.register(watcher, ENTRY_CREATE, ENTRY_DELETE, ENTRY_MODIFY);

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
