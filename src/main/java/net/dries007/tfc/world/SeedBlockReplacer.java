/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.world;

/**
 * An {@link IBlockReplacer} which is able to initialize custom noise layers to be used during placement.
 */
public abstract class SeedBlockReplacer implements IBlockReplacer
{
    private long cachedSeed;
    private boolean initialized;

    protected SeedBlockReplacer()
    {
        this.initialized = false;
    }

    @Override
    public void setSeed(long seed)
    {
        if (!initialized || seed != cachedSeed)
        {
            initialized = true;
            cachedSeed = seed;
            initSeed(seed);
        }
    }

    protected abstract void initSeed(long seed);
}