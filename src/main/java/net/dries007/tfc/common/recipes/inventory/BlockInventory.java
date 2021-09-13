/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.recipes.inventory;

import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.core.BlockPos;

import net.dries007.tfc.common.recipes.IBlockRecipe;

/**
 * This is a version of {@link net.minecraftforge.items.wrapper.RecipeWrapper} that is intended to be used for {@link IBlockRecipe}.
 * It extends {@link ItemStackInventory} for ease of use, and so the block can be visible (via the proxy stack)
 */
public class BlockInventory implements EmptyInventory
{
    protected final BlockPos pos;
    protected BlockState state;

    public BlockInventory(BlockPos pos, BlockState state)
    {
        this.pos = pos;
        this.state = state;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public BlockState getState()
    {
        return state;
    }

    public static class Mutable extends BlockInventory
    {
        private final BlockPos.MutableBlockPos cursor;

        public Mutable()
        {
            this(new BlockPos.MutableBlockPos());
        }

        private Mutable(BlockPos.MutableBlockPos pos)
        {
            super(pos, Blocks.AIR.defaultBlockState()); // Since the position is not expected to be initialized, we set a default null block state
            this.cursor = pos;
        }

        public void update(int x, int y, int z, BlockState state)
        {
            this.cursor.set(x, y, z);
            this.state = state;
        }
    }
}