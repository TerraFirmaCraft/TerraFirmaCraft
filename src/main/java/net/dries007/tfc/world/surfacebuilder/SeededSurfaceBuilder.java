/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.surfacebuilder;

import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import com.mojang.serialization.Codec;

public abstract class SeededSurfaceBuilder<C extends SurfaceBuilderConfig> extends SurfaceBuilder<C>
{
    private long lastSeed;
    private boolean initialized;

    protected SeededSurfaceBuilder(Codec<C> codec)
    {
        super(codec);
    }

    @Override
    public void initNoise(long seed)
    {
        if (lastSeed != seed || !initialized)
        {
            initSeed(seed);

            lastSeed = seed;
            initialized = true;
        }
    }

    protected abstract void initSeed(long seed);
}