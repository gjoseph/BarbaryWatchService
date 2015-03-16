/*
 * Copyright 2007-2009 Sun Microsystems, Inc.  All Rights Reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.  Sun designates this
 * particular file as subject to the "Classpath" exception as provided
 * by Sun in the LICENSE file that accompanied this code.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Sun Microsystems, Inc., 4150 Network Circle, Santa Clara,
 * CA 95054 USA or visit www.sun.com if you need additional information or
 * have any questions.
 */

package com.barbarysoftware.watchservice;

/**
 * An event or a repeated event for an object that is registered with a {@link
 * WatchService}.
 *
 * <p> An event is classified by its {@link #kind() kind} and has a {@link
 * #count() count} to indicate the number of times that the event has been
 * observed. This allows for efficient representation of repeated events. The
 * {@link #context() context} method returns any context associated with
 * the event. In the case of a repeated event then the context is the same for
 * all events.
 *
 * <p> Watch events are immutable and safe for use by multiple concurrent
 * threads.
 *
 * @param   <T>     The type of the context object associated with the event
 *
 * @since 1.7
 */

public abstract class WatchEvent<T> {

    /**
     * An event kind, for the purposes of identification.
     *
     * @since 1.7
     * @see StandardWatchEventKind
     */
    public static interface Kind<T> {
        /**
         * Returns the name of the event kind.
         */
        String name();

        /**
         * Returns the type of the {@link WatchEvent#context context} value.
         */
        Class<T> type();
    }

    /**
     * Initializes a new instance of this class.
     */
    protected WatchEvent() { }

    /**
     * An event modifier that qualifies how a {@link Watchable} is registered
     * with a {@link WatchService}.
     *
     * <p> This release does not define any <em>standard</em> modifiers.
     *
     * @since 1.7
     * @see Watchable#register
     */
    public static interface Modifier {
        /**
         * @return the name of the modifier.
         */
        String name();
    }

    /**
     * Returns the event kind.
     *
     * @return  the event kind
     */
    public abstract Kind<T> kind();

    /**
     * Returns the event count. If the event count is greater than {@code 1}
     * then this is a repeated event.
     *
     * @return  the event count
     */
    public abstract int count();

    /**
     * Returns the context for the event.
     *
     * <p> In the case of {@link StandardWatchEventKind#ENTRY_CREATE ENTRY_CREATE},
     * {@link StandardWatchEventKind#ENTRY_DELETE ENTRY_DELETE}, and {@link
     * StandardWatchEventKind#ENTRY_MODIFY ENTRY_MODIFY} events the context is
     * a {@code File}.
     *
     * @return  the event context; may be {@code null}
     */
    public abstract T context();
}
