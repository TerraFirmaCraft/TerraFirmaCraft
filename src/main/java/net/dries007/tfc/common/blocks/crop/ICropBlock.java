/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks.crop;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blockentities.CropBlockEntity;
import net.dries007.tfc.common.blockentities.FarmlandBlockEntity;
import net.dries007.tfc.util.climate.ClimateRange;

/**
 * Interface for blocks that use {@link CropBlockEntity}
 */
public interface ICropBlock
{
    void growthTick(Level level, BlockPos pos, BlockState state, CropBlockEntity crop);

    /**
     * Get any artificial growth limit imposed by the current environment.
     * This applies to, i.e. two tall crops that have a non-air block occupying their second position.
     *
     * @return A value between [0, 1].
     */
    default float getGrowthLimit(Level level, BlockPos pos, BlockState state)
    {
        return CropHelpers.lightValid(level, pos) ? CropHelpers.GROWTH_LIMIT : 0f;
    }

    void die(Level level, BlockPos pos, BlockState state, boolean fullyGrown);

    ClimateRange getClimateRange();

    FarmlandBlockEntity.NutrientType getPrimaryNutrient();

}
