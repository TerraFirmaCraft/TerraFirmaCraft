/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.forge;

import java.util.Arrays;

import it.unimi.dsi.fastutil.ints.IntArrayFIFOQueue;
import it.unimi.dsi.fastutil.ints.IntPriorityQueue;
import org.jetbrains.annotations.Nullable;

public enum ForgeStep
{
    HIT_LIGHT(-3, 53, 50, 128, 192),
    HIT_MEDIUM(-6, 71, 50, 160, 192),
    HIT_HARD(-9, 53, 68, 192, 192),
    DRAW(-15, 71, 68, 224, 192),
    PUNCH(2, 89, 50, 128, 224),
    BEND(7, 107, 50, 160, 224),
    UPSET(13, 89, 68, 192, 224),
    SHRINK(16, 107, 68, 224, 224);

    public static final int LIMIT = 150;

    private static final ForgeStep[] VALUES = values();
    private static final int[] PATHS;

    static
    {
        PATHS = new int[LIMIT];
        Arrays.fill(PATHS, -1);
        PATHS[0] = 0;

        final IntPriorityQueue queue = new IntArrayFIFOQueue();
        int reached = 1;
        queue.enqueue(0);
        for (int steps = 1; reached < LIMIT; steps++)
        {
            final int value = queue.dequeueInt();
            for (ForgeStep step : VALUES)
            {
                final int nextValue = value + step.step;
                if (nextValue >= 0 && nextValue < LIMIT && PATHS[nextValue] == -1)
                {
                    PATHS[nextValue] = steps;
                    reached++;
                }
            }
        }
    }

    @Nullable
    public static ForgeStep valueOf(int id)
    {
        return id >= 0 && id < VALUES.length ? VALUES[id] : null;
    }

    public static int getOptimalStepsToTarget(int target)
    {
        return PATHS[target];
    }

    private final int step;
    private final int x, y, u, v;

    ForgeStep(int step, int x, int y, int u, int v)
    {
        this.step = step;
        this.x = x;
        this.y = y;
        this.u = u;
        this.v = v;
    }

    public int step()
    {
        return step;
    }

    public int x()
    {
        return x;
    }

    public int y()
    {
        return y;
    }

    public int u()
    {
        return u;
    }

    public int v()
    {
        return v;
    }
}