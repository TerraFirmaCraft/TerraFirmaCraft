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
        long mixRight = FastRandom.mix(right, right);
        mixRight = FastRandom.mix(mixRight, right);
        mixRight = FastRandom.mix(mixRight, right);
        long mixLeft = FastRandom.mix(left, mixRight);
        mixLeft = FastRandom.mix(mixLeft, mixRight);
        return FastRandom.mix(mixLeft, mixRight);
    }

    public FastAreaContext(long seed, long seedModifier)
    {
        this.seed = mixSeed(seed, seedModifier);
    }

    @Override
    public void setPosition(long x, long z)
    {
        long value = this.seed;
        value = FastRandom.mix(value, x);
        value = FastRandom.mix(value, z);
        value = FastRandom.mix(value, x);
        value = FastRandom.mix(value, z);
        this.rval = value;
    }

    @Override
    public FastArea makeArea(IPixelTransformer pixelTransformer)
    {
        return new FastArea(pixelTransformer, 256);
    }

    @Override
    public int random(int bound)
    {
        final int value = (int)Math.floorMod(rval >> 24, bound);
        rval = FastRandom.mix(rval, seed);
        return value;
    }

    @Override
    public ImprovedNoiseGenerator getNoiseGenerator()
    {
        throw new IllegalStateException("Go away");
    }
}
