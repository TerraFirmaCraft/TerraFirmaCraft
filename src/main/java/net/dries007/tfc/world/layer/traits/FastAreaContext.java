/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.layer.traits;


import net.minecraft.util.FastRandom;
import net.minecraft.world.gen.IExtendedNoiseRandom;
import net.minecraft.world.gen.ImprovedNoiseGenerator;
import net.minecraft.world.gen.layer.traits.IPixelTransformer;

public class FastAreaContext implements IExtendedNoiseRandom<FastArea>
{
    private final long seed;
    private long rval;

    private static long mixSeed(long left, long right)
    {
        long mixRight = FastRandom.next(right, right);
        mixRight = FastRandom.next(mixRight, right);
        mixRight = FastRandom.next(mixRight, right);
        long mixLeft = FastRandom.next(left, mixRight);
        mixLeft = FastRandom.next(mixLeft, mixRight);
        return FastRandom.next(mixLeft, mixRight);
    }

    public FastAreaContext(long seed, long seedModifier)
    {
        this.seed = mixSeed(seed, seedModifier);
    }

    @Override
    public void initRandom(long x, long z)
    {
        long value = this.seed;
        value = FastRandom.next(value, x);
        value = FastRandom.next(value, z);
        value = FastRandom.next(value, x);
        value = FastRandom.next(value, z);
        this.rval = value;
    }

    @Override
    public FastArea createResult(IPixelTransformer pixelTransformer)
    {
        return new FastArea(pixelTransformer, 256);
    }

    @Override
    public int nextRandom(int bound)
    {
        final int value = (int)Math.floorMod(rval >> 24, bound);
        rval = FastRandom.next(rval, seed);
        return value;
    }

    @Override
    public ImprovedNoiseGenerator getBiomeNoise()
    {
        throw new IllegalStateException("Go away");
    }
}
