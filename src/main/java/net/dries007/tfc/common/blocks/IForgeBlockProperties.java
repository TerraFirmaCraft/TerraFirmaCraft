/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import javax.annotation.Nullable;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.core.Direction;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockGetter;
import net.minecraftforge.common.extensions.IForgeBlock;

/**
 * This implements some of the more annoying methods in {@link IForgeBlock} which would otherwise require implementing across all manner of vanilla subclasses.
 * Since forge has made the decision that blocks should have behavioral control rather than add entries to {@link net.minecraft.world.level.block.state.BlockBehaviour}, we mimic the same structure here.
 */
public interface IForgeBlockProperties extends IForgeBlock
{
    ForgeBlockProperties getForgeProperties();

    // todo: this needs to be moved to the EntityBlock interface
    default boolean hasTileEntity(BlockState state)
    {
        return getForgeProperties().hasTileEntity();
    }

    @Nullable
    default BlockEntity createTileEntity(BlockState state, BlockGetter world)
    {
        return getForgeProperties().createTileEntity();
    }

    @Override
    default int getFlammability(BlockState state, BlockGetter world, BlockPos pos, Direction face)
    {
        return getForgeProperties().getFlammability();
    }

    @Override
    default int getFireSpreadSpeed(BlockState state, BlockGetter world, BlockPos pos, Direction face)
    {
        return getForgeProperties().getFireSpreadSpeed();
    }
}
