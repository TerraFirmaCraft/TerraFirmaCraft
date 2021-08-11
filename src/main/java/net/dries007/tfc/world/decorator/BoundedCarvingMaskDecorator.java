/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.decorator;

import java.util.BitSet;
import java.util.Random;
import java.util.stream.IntStream;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;

import com.mojang.serialization.Codec;

public class BoundedCarvingMaskDecorator extends FeatureDecorator<BoundedCarvingMaskConfig>
{
    public BoundedCarvingMaskDecorator(Codec<BoundedCarvingMaskConfig> codec)
    {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecorationContext helper, Random rand, BoundedCarvingMaskConfig config, BlockPos pos)
    {
        final ChunkPos chunkPos = new ChunkPos(pos);
        final BitSet carvingMask = helper.getCarvingMask(chunkPos, config.step());
        return IntStream.range(config.minY() << 8, config.maxY() << 8)
            .filter(i -> carvingMask.get(i) && rand.nextFloat() < config.probability())
            .mapToObj(i -> {
                final int x = i & 15;
                final int z = i >> 4 & 15;
                final int y = i >> 8;
                return new BlockPos(chunkPos.getMinBlockX() + x, y, chunkPos.getMinBlockZ() + z);
            });
    }
}
