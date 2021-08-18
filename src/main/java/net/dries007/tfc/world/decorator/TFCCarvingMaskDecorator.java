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
import net.dries007.tfc.world.carver.CarverHelpers;

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
    public Stream<BlockPos> getPositions(DecorationContext context, Random rand, TFCCarvingMaskConfig config, BlockPos pos)
    {
        final ChunkPos chunkPos = new ChunkPos(pos);
        final BitSet carvingMask = context.getCarvingMask(chunkPos, config.step());
        final int minY = context.getLevel().dimensionType().minY();

        final int configMinY = config.minY().resolveY(context);
        final int configMaxY = config.maxY().resolveY(context);

        final int minIndex = CarverHelpers.maskIndex(0, configMinY, 0, minY);
        final int maxIndex = CarverHelpers.maskIndex(0, 1 + configMaxY, 0, minY);

        return IntStream.range(minIndex, Math.min(maxIndex, carvingMask.length()))
            .filter(index -> carvingMask.get(index) && rand.nextFloat() < config.probability())
            .mapToObj(index -> {
                final int x = index & 15;
                final int z = index >> 4 & 15;
                final int y = (index >> 8) + minY;
                return new BlockPos(chunkPos.getMinBlockX() + x, y, chunkPos.getMinBlockZ() + z);
            });
    }
}
