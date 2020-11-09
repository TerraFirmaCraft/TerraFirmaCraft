package net.dries007.tfc.world.decorator;

import java.util.BitSet;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.gen.feature.WorldDecoratingHelper;
import net.minecraft.world.gen.placement.Placement;

import com.mojang.serialization.Codec;

public class BoundedCarvingMaskDecorator extends Placement<BoundedCarvingMaskConfig>
{
    public BoundedCarvingMaskDecorator(Codec<BoundedCarvingMaskConfig> codec)
    {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(WorldDecoratingHelper helper, Random rand, BoundedCarvingMaskConfig config, BlockPos pos)
    {
        final ChunkPos chunkPos = new ChunkPos(pos);
        final BitSet carvingMask = helper.getCarvingMask(chunkPos, config.step);
        return IntStream.range(0, carvingMask.length())
            .filter(i -> {
                final int y = (i >> 8);
                return carvingMask.get(i) && y >= config.minY && y <= config.maxY && rand.nextFloat() < config.probability;
            })
            .mapToObj(i -> {
                int x = i & 15;
                int z = i >> 4 & 15;
                int y = i >> 8;
                return new BlockPos(chunkPos.getMinBlockX() + x, y, chunkPos.getMinBlockZ() + z);
            });
    }
}
