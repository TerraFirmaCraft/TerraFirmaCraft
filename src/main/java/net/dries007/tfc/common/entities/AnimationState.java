/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import java.util.function.Consumer;

import net.minecraft.util.Mth;

public class AnimationState
{
    private static final long IMMOBILE = Long.MAX_VALUE;

    private long lastTime = Long.MAX_VALUE;
    private long accumulatedTime;

    public void start(int ticks)
    {
        this.lastTime = (long) ticks * 1000L / 20L;
        this.accumulatedTime = 0L;
    }

    public void startIfStopped(int ticks)
    {
        if (!isStarted()) start(ticks);
    }

    public void stop()
    {
        this.lastTime = IMMOBILE;
    }

    public void ifStarted(Consumer<AnimationState> consumer)
    {
        if (isStarted()) consumer.accept(this);
    }

    public void updateTime(float gameTime, float speed)
    {
        if (this.isStarted())
        {
            long ticks = Mth.lfloor(gameTime * 1000f / 20f);
            this.accumulatedTime += (long) ((float) (ticks - this.lastTime) * speed);
            this.lastTime = ticks;
        }
    }

    public long getAccumulatedTime()
    {
        return this.accumulatedTime;
    }

    public boolean isStarted()
    {
        return this.lastTime != IMMOBILE;
    }
}
