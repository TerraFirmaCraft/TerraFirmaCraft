package net.dries007.tfc.objects.recipes;

import net.minecraft.block.BlockState;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockReader;

/**
 * This is a version of {@link net.minecraftforge.items.wrapper.RecipeWrapper} that is intended to be used for {@link IBlockRecipe}.
 * It extends {@link ItemStackRecipeWrapper} for ease of use, and so the block can be visible (via the proxy stack)
 */
public class BlockRecipeWrapper extends ItemStackRecipeWrapper
{
    private final IBlockReader world;
    private final BlockPos pos;
    private final BlockState state;

    public BlockRecipeWrapper(IBlockReader world, BlockPos pos)
    {
        this(world, pos, world.getBlockState(pos));
    }

    public BlockRecipeWrapper(IBlockReader world, BlockPos pos, BlockState state)
    {
        super(new ItemStack(state.getBlock()));
        this.world = world;
        this.pos = pos;
        this.state = state;
    }

    public IBlockReader getWorld()
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
}
