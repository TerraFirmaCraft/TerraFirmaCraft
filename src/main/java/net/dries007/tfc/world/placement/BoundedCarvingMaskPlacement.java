/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import java.util.Random;
import java.util.stream.Stream;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.CarvingMask;
import net.minecraft.world.level.levelgen.GenerationStep;
import net.minecraft.world.level.levelgen.VerticalAnchor;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

/**
 * Extension of the vanilla carving mask decorator which allows min and max y bounds.
 */
public class BoundedCarvingMaskPlacement extends PlacementModifier
{
    public static final Codec<BoundedCarvingMaskPlacement> CODEC = RecordCodecBuilder.create(instance -> instance.group(
        VerticalAnchor.CODEC.optionalFieldOf("min_y", VerticalAnchor.bottom()).forGetter(c -> c.minY),
        VerticalAnchor.CODEC.optionalFieldOf("max_y", VerticalAnchor.top()).forGetter(c -> c.maxY),
        GenerationStep.Carving.CODEC.fieldOf("step").forGetter(c -> c.step)
    ).apply(instance, BoundedCarvingMaskPlacement::new));

    private final VerticalAnchor minY;
    private final VerticalAnchor maxY;
    private final GenerationStep.Carving step;

    public BoundedCarvingMaskPlacement(VerticalAnchor minY, VerticalAnchor maxY, GenerationStep.Carving step)
    {
        this.minY = minY;
        this.maxY = maxY;
        this.step = step;
    }

    @Override
    public PlacementModifierType<?> type()
    {
        return TFCPlacements.CARVING_MASK.get();
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, Random random, BlockPos pos)
    {
        final ChunkPos chunkPos = new ChunkPos(pos);
        final CarvingMask carvingMask = context.getCarvingMask(chunkPos, step);
        final int minY = this.minY.resolveY(context);
        final int maxY = this.maxY.resolveY(context);

        return carvingMask.stream(chunkPos).filter(p -> p.getY() >= minY && p.getY() <= maxY);
    }
}
