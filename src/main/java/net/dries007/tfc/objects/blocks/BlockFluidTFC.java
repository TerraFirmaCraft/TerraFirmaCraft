/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;

@ParametersAreNonnullByDefault
public class BlockFluidTFC extends BlockFluidClassic
{
    public BlockFluidTFC(Fluid fluid, Material material)
    {
        super(fluid, material);
    }

    public BlockFluidTFC(Fluid fluid, Material material, boolean canCreateSources)
    {
        this(fluid, material);
        this.canCreateSources = canCreateSources;
    }

    @Override
    public float getFluidHeightForRender(IBlockAccess world, BlockPos adjPos, @Nonnull IBlockState upState)
    {
        IBlockState adjState = world.getBlockState(adjPos);

        // any adjacent above matching liquids merge to 1
        if (isMergeableFluid(upState))
        {
            return 1;
        }

        // adjacent mergeable liquids
        if (isMergeableFluid(adjState))
        {
            Block adjBlock = adjState.getBlock();
            if (adjBlock == this || adjBlock instanceof BlockLiquid)
                return super.getFluidHeightForRender(world, adjPos, upState);
            else
                return ((BlockFluidBase) adjBlock).getFluidHeightForRender(world, adjPos, upState);
        }

        // adjacent solid
        if (adjState.getMaterial().isSolid())
            return -1;

        // adjacent air or non-mergeable liquids
        return 0;
    }

    protected boolean isMergeableFluid(@Nonnull IBlockState blockstate)
    {
        return (blockstate.getMaterial() == getDefaultState().getMaterial()) && (blockstate.getMaterial().isLiquid());
    }

    @Override
    public boolean canDisplace(IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        Block block = state.getBlock();

        if (block.isAir(state, world, pos))
        {
            return true;
        }

        if (block == this)
        {
            return false;
        }

        if (displacements.containsKey(block))
        {
            return displacements.get(block);
        }

        Material material = state.getMaterial();
        if (material.blocksMovement() || material == Material.PORTAL || material == Material.STRUCTURE_VOID)
        {
            return false;
        }

        // this is where it differs from the source:

        if (block instanceof BlockFluidTFC)
        {
            return (state.getValue(LEVEL) != 0);
        }

        int density = getDensity(world, pos);
        if (density == Integer.MAX_VALUE)
        {
            return true;
        }

        return this.density > density;
    }

    @Override
    public boolean isSourceBlock(IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        return isMergeableFluid(state) && state.getValue(LEVEL) == 0;
    }

    @Override
    protected boolean canFlowInto(IBlockAccess world, BlockPos pos)
    {
        IBlockState state = world.getBlockState(pos);
        return super.canFlowInto(world, pos) || state.getMaterial().isLiquid();
    }
}
