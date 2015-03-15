package com.barbarysoftware.watchservice;

import java.io.Closeable;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

public abstract class WatchService implements Closeable {

    protected WatchService() {
    }

    /**
     * Closes this watch service.
     * <p/>
     * <p> If a thread is currently blocked in the {@link #take take} or {@link
     * #poll(long,TimeUnit) poll} methods waiting for a key to be queued then
     * it immediately receives a {@link ClosedWatchServiceException}. Any
     * valid keys associated with this watch service are {@link WatchKey#isValid
     * invalidated}.
     * <p/>
     * <p> After a watch service is closed, any further attempt to invoke
     * operations upon it will throw {@link ClosedWatchServiceException}.
     * If this watch service is already closed then invoking this method
     * has no effect.
     *
     * @throws IOException if an I/O error occurs
     */
    @Override
    public abstract void close() throws IOException;

    /**
     * Retrieves and removes the next watch key, or {@code null} if none are
     * present.
     *
     * @return the next watch key, or {@code null}
     * @throws ClosedWatchServiceException if this watch service is closed
     */
    public abstract WatchKey poll();

    /**
     * Retrieves and removes the next watch key, waiting if necessary up to the
     * specified wait time if none are yet present.
     *
     * @param timeout how to wait before giving up, in units of unit
     * @param unit    a {@code TimeUnit} determining how to interpret the timeout
     *                parameter
     * @return the next watch key, or {@code null}
     * @throws ClosedWatchServiceException if this watch service is closed, or it is closed while waiting
     *                                     for the next key
     * @throws InterruptedException        if interrupted while waiting
     */
    public abstract WatchKey poll(long timeout, TimeUnit unit)
            throws InterruptedException;

    /**
     * Retrieves and removes next watch key, waiting if none are yet present.
     *
     * @return the next watch key
     * @throws ClosedWatchServiceException if this watch service is closed, or it is closed while waiting
     *                                     for the next key
     * @throws InterruptedException        if interrupted while waiting
     */
    public abstract WatchKey take() throws InterruptedException;

    public static WatchService newWatchService() {
        final String osVersion = System.getProperty("os.version");
        final int minorVersion = Integer.parseInt(osVersion.split("\\.")[1]);
        if (minorVersion < 5) {
            // for Mac OS X prior to Leopard (10.5)
            return new MacOSXPollingWatchService();
            
        } else {
            // for Mac OS X Leopard (10.5) and upwards
            return new MacOSXListeningWatchService();
        }
    }

}
