package net.dries007.tfc.util;

import java.util.ArrayList;
import java.util.List;

/**
 * This is a simple builder style wrapper around a delayed task executor
 * It's used in places where we need to take into account suppliers (for instance, biome constructors need to access registry objects, but can't)
 */
public class DelayedRunnable implements Runnable
{
    private final List<Runnable> tasks;

    public DelayedRunnable()
    {
        this.tasks = new ArrayList<>();
    }

    public void enqueue(Runnable runnable)
    {
        this.tasks.add(runnable);
    }

    @Override
    public void run()
    {
        this.tasks.forEach(Runnable::run);
        this.tasks.clear();
    }
}
