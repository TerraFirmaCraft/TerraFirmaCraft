/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.decorator;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.feature.configurations.DecoratorConfiguration;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

import com.mojang.serialization.Codec;

public abstract class SeededDecorator<C extends DecoratorConfiguration> extends FeatureDecorator<C>
{
    private long cachedSeed;
    private boolean initialized;

    protected SeededDecorator(Codec<C> codec)
    {
        super(codec);
    }

    @Override
    public final Stream<BlockPos> getPositions(DecorationContext context, Random rand, C config, BlockPos pos)
    {
        long seed = context.getLevel().getSeed();
        if (!initialized || cachedSeed != seed)
        {
            initSeed(seed);
            cachedSeed = seed;
            initialized = true;
        }
        return getSeededPositions(context, rand, config, pos);
    }

    protected abstract void initSeed(long seed);

    protected abstract Stream<BlockPos> getSeededPositions(DecorationContext helper, Random rand, C config, BlockPos pos);
}
