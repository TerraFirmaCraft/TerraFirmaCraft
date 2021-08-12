package net.dries007.tfc.common.blocks;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.common.blocks.ForgeBlockProperties;

/**
 * Borrowed from {@link net.minecraft.world.level.block.BaseEntityBlock}
 */
public interface EntityBlockExtension extends EntityBlock
{
    /**
     * Implement with {@link net.dries007.tfc.common.blocks.IForgeBlockExtension}
     */
    ForgeBlockProperties getForgeProperties();

    @Nullable
    @Override
    default <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> givenType)
    {
        return getForgeProperties().getTicker(level, state, givenType);
    }

    @Nullable
    @Override
    default BlockEntity newBlockEntity(BlockPos pos, BlockState state)
    {
        return getForgeProperties().newBlockEntity(pos, state);
    }
}
