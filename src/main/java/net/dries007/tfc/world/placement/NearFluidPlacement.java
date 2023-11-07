/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.placement;

import java.util.List;
import java.util.stream.Stream;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.levelgen.placement.PlacementContext;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.PlacementModifierType;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.FluidState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.world.Codecs;

public class NearFluidPlacement extends PlacementModifier
{
    public static final Codec<NearFluidPlacement> PLACEMENT_CODEC = RecordCodecBuilder.create(instance -> instance.group(
        ExtraCodecs.NON_NEGATIVE_INT.optionalFieldOf("radius", 2).forGetter(c -> c.radius),
        Codecs.FLUID.listOf().optionalFieldOf("fluids", null).forGetter(c -> c.fluids)
    ).apply(instance, NearFluidPlacement::new));

    private final int radius;
    @Nullable private final List<Fluid> fluids;

    public NearFluidPlacement(int radius, @Nullable List<Fluid> fluids)
    {
        this.radius = radius;
        this.fluids = fluids;
    }

    @Override
    public PlacementModifierType<?> type()
    {
        return TFCPlacements.NEAR_FLUID.get();
    }

    @Override
    public Stream<BlockPos> getPositions(PlacementContext context, RandomSource random, BlockPos pos)
    {
        final BlockPos.MutableBlockPos mutablePos = new BlockPos.MutableBlockPos();
        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                for (int y = 0; y >= -radius; y--)
                {
                    mutablePos.set(pos).move(x, y, z);

                    final FluidState state = context.getBlockState(mutablePos).getFluidState();
                    if (fluids == null ? !state.isEmpty() : fluids.contains(state.getType()))
                    {
                        return Stream.of(pos);
                    }
                }
            }
        }
        return Stream.empty();
    }
}
