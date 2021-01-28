/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
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