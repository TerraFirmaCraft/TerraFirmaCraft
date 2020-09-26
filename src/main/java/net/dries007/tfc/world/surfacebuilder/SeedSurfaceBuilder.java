/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world.surfacebuilder;

import java.util.function.Function;

import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;

import com.mojang.datafixers.Dynamic;

public abstract class SeedSurfaceBuilder<C extends SurfaceBuilderConfig> extends SurfaceBuilder<C>
{
    private long lastSeed;
    private boolean initialized;

    protected SeedSurfaceBuilder(Function<Dynamic<?>, ? extends C> configFactory)
    {
        super(configFactory);
    }

    @Override
    public void setSeed(long seed)
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
