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

/**
 * The vanilla one is bad
 * - It does not properly handle extended y values
 * - It does not allow y bounds at all
 */
public class TFCCarvingMaskDecorator extends FeatureDecorator<TFCCarvingMaskConfig>
{
    public TFCCarvingMaskDecorator(Codec<TFCCarvingMaskConfig> codec)
    {
        super(codec);
    }

    @Override
    public Stream<BlockPos> getPositions(DecorationContext helper, Random rand, TFCCarvingMaskConfig config, BlockPos pos)
    {
        final int minY = helper.getLevel().dimensionType().minY();
        final ChunkPos chunkPos = new ChunkPos(pos);
        final BitSet carvingMask = helper.getCarvingMask(chunkPos, config.step());
        final int length = Math.min((config.maxY() - config.minY() + 1) << 8, carvingMask.length());
        return IntStream.range(0, length)
            .filter(index -> carvingMask.get(index) && rand.nextFloat() < config.probability())
            .mapToObj(index -> {
                final int x = index & 15;
                final int z = index >> 4 & 15;
                final int y = (index >> 8) - minY;
                return new BlockPos(chunkPos.getMinBlockX() + x, y, chunkPos.getMinBlockZ() + z);
            });
    }
}
