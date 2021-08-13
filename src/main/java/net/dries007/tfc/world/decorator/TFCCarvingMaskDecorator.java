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
    public Stream<BlockPos> getPositions(DecorationContext helper, Random rand, TFCCarvingMaskConfig config, BlockPos pos)
    {
        final ChunkPos chunkPos = new ChunkPos(pos);
        final BitSet carvingMask = helper.getCarvingMask(chunkPos, config.step());

        final int minY = helper.getLevel().dimensionType().minY();
        final int maxY = minY + helper.getLevel().dimensionType().height() - 1;

        int configMinY = config.minY() == Integer.MIN_VALUE ? minY : config.minY();
        final int configMaxY = config.maxY() == Integer.MAX_VALUE ? maxY : config.maxY();

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
