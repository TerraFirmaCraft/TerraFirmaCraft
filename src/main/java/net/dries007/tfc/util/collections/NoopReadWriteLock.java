/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util.collections;

import java.util.Date;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import org.jetbrains.annotations.NotNull;

/**
 * Not for usage in multithreading!
 */
public enum NoopReadWriteLock implements ReadWriteLock
{
    INSTANCE;

    @Override
    @NotNull
    public Lock readLock()
    {
        return NotALock.INSTANCE;
    }

    @Override
    @NotNull
    public Lock writeLock()
    {
        return NotALock.INSTANCE;
    }

    private enum NotALock implements Lock
    {
        INSTANCE;

        @Override
        public void lock() { }

        @Override
        public void lockInterruptibly() { }

        @Override
        public boolean tryLock()
        {
            return true;
        }

        @Override
        public boolean tryLock(long time, @NotNull TimeUnit unit)
        {
            return true;
        }

        @Override
        public void unlock() { }

        @NotNull
        @Override
        public Condition newCondition()
        {
            return NotACondition.INSTANCE;
        }
    }

    private enum NotACondition implements Condition
    {
        INSTANCE;

        @Override
        public void await() {}

        @Override
        public void awaitUninterruptibly() { }

        @Override
        public long awaitNanos(long nanosTimeout)
        {
            return 0;
        }

        @Override
        public boolean await(long time, TimeUnit unit)
        {
            return true;
        }

        @Override
        public boolean awaitUntil(@NotNull Date deadline)
        {
            return true;
        }

        @Override
        public void signal() { }

        @Override
        public void signalAll() { }
    }
}
