/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.plant;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.ExtendedProperties;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.registry.RegistryPlant;

public abstract class TFCPassableCactusBlock extends TFCTallGrassBlock
{
    public static TFCPassableCactusBlock create(RegistryPlant plant, ExtendedProperties properties)
    {
        return new TFCPassableCactusBlock(properties)
        {
            @Override
            public RegistryPlant getPlant()
            {
                return plant;
            }
        };
    }

    protected TFCPassableCactusBlock(ExtendedProperties properties)
    {
        super(properties);
    }

    @Override
    @SuppressWarnings("deprecation")
    protected boolean canSurvive(BlockState state, LevelReader level, BlockPos pos)
    {
        final BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();

        mutable.setWithOffset(pos, 0, -1, 0);
        BlockState belowState = level.getBlockState(mutable);
        if (state.getValue(PART) == Part.LOWER)
        {
            return Helpers.isBlock(belowState, TFCTags.Blocks.DRY_PLANT_PLANTABLE_ON);
        }
        else
        {
            if (state.getBlock() != this)
            {
                return Helpers.isBlock(belowState, TFCTags.Blocks.DRY_PLANT_PLANTABLE_ON);
            }
            return belowState.getBlock() == this && belowState.getValue(PART) == Part.LOWER;
        }
    }

    @Override
    protected void entityInside(BlockState state, Level level, BlockPos pos, Entity entity)
    {
        entity.hurt(entity.damageSources().cactus(), 1f);
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType pathComputationType)
    {
        return false;
    }
}
