/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import net.dries007.tfc.common.blocks.plant.CreepingPlantBlock;
import net.dries007.tfc.util.EnvironmentHelpers;

public class CreepingPlantFeature extends Feature<CreepingPlantConfig>
{
    public CreepingPlantFeature(Codec<CreepingPlantConfig> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<CreepingPlantConfig> context)
    {
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin();
        final BlockState state = context.config().block().defaultBlockState();
        final int radius = context.config().radius();
        final int height = context.config().height();
        final BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos();

        for (int x = -radius; x <= radius; x++)
        {
            for (int z = -radius; z <= radius; z++)
            {
                for (int y = 0; y < height; y++)
                {
                    if (x * x + z + z < radius * radius && context.random().nextFloat() < context.config().integrity())
                    {
                        cursor.setWithOffset(pos, x, y, z);
                        if (EnvironmentHelpers.isWorldgenReplaceable(level, cursor))
                        {
                            final BlockState newState = CreepingPlantBlock.updateStateFromSides(level, cursor, state);
                            if (!newState.isAir())
                            {
                                setBlock(level, cursor, newState);
                            }
                        }
                    }
                }
            }
        }

        return false;
    }
}
