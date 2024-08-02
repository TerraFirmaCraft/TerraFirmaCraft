/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

/**
 * Borrowed from {@link net.minecraft.world.level.block.BaseEntityBlock}
 */
public interface EntityBlockExtension extends EntityBlock, IForgeBlockExtension
{
    /**
     * Implement with {@link net.dries007.tfc.common.blocks.IForgeBlockExtension}
     */
    ExtendedProperties getExtendedProperties();

    @Nullable
    @Override
    default BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return getExtendedProperties().newBlockEntity(pos, state);
    }

    @Nullable
    @Override
    default <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> givenType)
    {
        return getExtendedProperties().getTicker(level, givenType);
    }
}
