/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.world.feature.plant;

import com.mojang.serialization.Codec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.FeaturePlaceContext;

import net.dries007.tfc.common.blocks.plant.EpiphytePlantBlock;
import net.dries007.tfc.util.EnvironmentHelpers;
import net.dries007.tfc.world.feature.BlockConfig;

public class EpiphytePlantFeature extends Feature<BlockConfig<EpiphytePlantBlock>>
{
    public static final Codec<BlockConfig<EpiphytePlantBlock>> CODEC = BlockConfig.codec(b -> b instanceof EpiphytePlantBlock t ? t : null, "Must be a " + EpiphytePlantBlock.class.getSimpleName());

    public EpiphytePlantFeature(Codec<BlockConfig<EpiphytePlantBlock>> codec)
    {
        super(codec);
    }

    @Override
    public boolean place(FeaturePlaceContext<BlockConfig<EpiphytePlantBlock>> context)
    {
        final RandomSource random = context.random();
        final WorldGenLevel level = context.level();
        final BlockPos pos = context.origin().offset(0, random.nextInt(12), 0);
        if (!EnvironmentHelpers.isWorldgenReplaceable(level, pos))
            return false;
        BlockState state = context.config().block().defaultBlockState();
        for (Direction direction : Direction.Plane.HORIZONTAL)
        {
            state = state.setValue(EpiphytePlantBlock.FACING, direction);
            if (state.canSurvive(level, pos))
            {
                setBlock(level, pos, state);
                return true;
            }
        }
        return false;
    }
}
