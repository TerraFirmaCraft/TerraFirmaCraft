package net.dries007.tfc.world.decorator;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.util.math.BlockPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;

import com.mojang.serialization.Codec;
import net.dries007.tfc.world.biome.VolcanoNoise;
import net.dries007.tfc.world.noise.INoise2D;
import net.dries007.tfc.world.noise.ITyped2D;

public class VolcanoDecorator extends SeededDecorator<VolcanoConfig>
{
    private ITyped2D<BlockPos> centerNoise;
    private INoise2D easingNoise;

    public VolcanoDecorator(Codec<VolcanoConfig> codec)
    {
        super(codec);
    }

    @Override
    protected void initSeed(long seed)
    {
        easingNoise = VolcanoNoise.easing(seed);
        centerNoise = VolcanoNoise.centers(seed);
    }

    @Override
    protected Stream<BlockPos> getSeededPositions(WorldDecoratingHelper helper, Random rand, VolcanoConfig config, BlockPos pos)
    {
        if (config.useCenter())
        {
            BlockPos centerPos = centerNoise.typed(pos.getX(), pos.getZ());
            if (centerPos.getX() >> 4 == pos.getX() >> 4 && centerPos.getZ() >> 4 == pos.getZ() >> 4)
            {
                return Stream.of(centerPos);
            }
        }
        else
        {
            if (easingNoise.noise(pos.getX(), pos.getZ()) > config.getDistance())
            {
                return Stream.of(pos);
            }
        }
        return Stream.empty();
    }
}
