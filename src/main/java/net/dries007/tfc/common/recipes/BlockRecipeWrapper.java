/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.recipes;

import net.minecraft.block.BlockState;
import net.minecraft.block.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

/**
 * This is a version of {@link net.minecraftforge.items.wrapper.RecipeWrapper} that is intended to be used for {@link IBlockRecipe}.
 * It extends {@link ItemStackRecipeWrapper} for ease of use, and so the block can be visible (via the proxy stack)
 */
public class BlockRecipeWrapper extends ItemStackRecipeWrapper
{
    protected final World world;
    protected final BlockPos pos;
    protected BlockState state;

    public BlockRecipeWrapper(World world, BlockPos pos)
    {
        this(world, pos, world.getBlockState(pos));
    }

    public BlockRecipeWrapper(World world, BlockPos pos, BlockState state)
    {
        super(new ItemStack(state.getBlock()));
        this.world = world;
        this.pos = pos;
        this.state = state;
    }

    public World getWorld()
    {
        return world;
    }

    public BlockPos getPos()
    {
        return pos;
    }

    public BlockState getState()
    {
        return state;
    }

    public static class Mutable extends BlockRecipeWrapper
    {
        private final BlockPos.Mutable mutablePos;

        public Mutable(World world)
        {
            this(world, new BlockPos.Mutable());
        }

        private Mutable(World world, BlockPos.Mutable pos)
        {
            super(world, pos, Blocks.AIR.getDefaultState()); // Since the position is not expected to be initialized, we set a default null block state

            this.mutablePos = pos;
        }

        public void setPos(int x, int y, int z, BlockState state)
        {
            this.mutablePos.setPos(x, y, z);
            this.state = state;
        }
    }
}
