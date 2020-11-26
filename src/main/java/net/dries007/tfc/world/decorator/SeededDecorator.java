package net.dries007.tfc.world.decorator;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.IPlacementConfig;
import net.minecraft.world.gen.placement.Placement;

import com.mojang.serialization.Codec;
import net.dries007.tfc.mixin.world.gen.feature.WorldDecoratingHelperAccessor;

public abstract class SeededDecorator<C extends IPlacementConfig> extends Placement<C>
{
    private long cachedSeed;
    private boolean initialized;

    protected SeededDecorator(Codec<C> codec)
    {
        super(codec);
    }

    @Override
    public final Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random rand, C config, BlockPos pos)
    {
        long seed = ((WorldDecoratingHelperAccessor) helper).accessor$getLevel().getSeed();
        if (!initialized || cachedSeed != seed)
        {
            initSeed(seed);
            cachedSeed = seed;
            initialized = true;
        }
        return getSeededPositions(helper, rand, config, pos);
    }

    protected abstract void initSeed(long seed);

    protected abstract Stream<BlockPos> getSeededPositions(WorldDecoratingHelper helper, Random rand, C config, BlockPos pos);
}
