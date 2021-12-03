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
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.DecorationContext;
import net.minecraft.world.level.levelgen.placement.FeatureDecorator;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.dries007.tfc.world.carver.CarverHelpers;

/**
 * The vanilla one is bad
 * - It does not properly handle extended y values
 * - It does not allow y bounds at all
 */
public class TFCCarvingMaskDecorator extends PlacementModifier
{
    public static final Codec<TFCCarvingMaskDecorator> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        VerticalAnchor.CODEC.optionalFieldOf("min_y", VerticalAnchor.bottom()).forGetter(c -> c.minY),
        VerticalAnchor.CODEC.optionalFieldOf("max_y", VerticalAnchor.top()).forGetter(c -> c.maxY),
        GenerationStep.Carving.CODEC.fieldOf("step").forGetter(c -> c.step)
    ).apply(instance, TFCCarvingMaskDecorator::new));

    private final VerticalAnchor minY;
    private final VerticalAnchor maxY;
    private final GenerationStep.Carving step;

    public TFCCarvingMaskDecorator(VerticalAnchor minY, VerticalAnchor maxY, GenerationStep.Carving step)
    {
        this.minY = minY;
        this.maxY = maxY;
        this.step = step;
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, Random random, BlockPos pos)
    {
        final ChunkPos chunkPos = new ChunkPos(pos);
        final CarvingMask carvingMask = context.getCarvingMask(chunkPos, step);
        final int minY = context.getLevel().dimensionType().minY();

        final int configMinY = this.minY.resolveY(context);
        final int configMaxY = this.maxY.resolveY(context);

        final int minIndex = CarverHelpers.maskIndex(0, configMinY, 0, minY);
        final int maxIndex = CarverHelpers.maskIndex(0, 1 + configMaxY, 0, minY);

        return IntStream.range(minIndex, Math.min(maxIndex, carvingMask.length()))
            .filter(index -> carvingMask.get(index))
            .mapToObj(index -> {
                final int x = index & 15;
                final int z = index >> 4 & 15;
                final int y = (index >> 8) + minY;
                return new BlockPos(chunkPos.getMinBlockX() + x, y, chunkPos.getMinBlockZ() + z);
            });
    }
}
