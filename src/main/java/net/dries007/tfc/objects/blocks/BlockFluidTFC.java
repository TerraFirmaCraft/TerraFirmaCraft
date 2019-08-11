/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.BlockFluidClassic;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.Constants;
import net.dries007.tfc.api.capability.food.FoodStatsTFC;
import net.dries007.tfc.objects.fluids.FluidsTFC;

@ParametersAreNonnullByDefault
public class BlockFluidTFC extends BlockFluidClassic
{
    public BlockFluidTFC(Fluid fluid, Material material, boolean canCreateSources)
    {
        super(fluid, material);
        this.canCreateSources = canCreateSources;
    }

    @SideOnly(Side.CLIENT)
    @Override
    public void randomDisplayTick(IBlockState stateIn, World worldIn, BlockPos pos, Random rand)
    {
        if (definedFluid == FluidsTFC.HOT_WATER.get() && rand.nextInt(4) == 0)
        {
            worldIn.spawnParticle(EnumParticleTypes.WATER_BUBBLE, (double) (pos.getX() + rand.nextFloat()), pos.getY() + 0.50D, (double) (pos.getZ() + rand.nextFloat()), 0.0D, 0.0D, 0.0D, Block.getStateId(stateIn));
        }
    }

    @Override
    public void onEntityCollision(World worldIn, BlockPos pos, IBlockState state, Entity entityIn)
    {
        super.onEntityCollision(worldIn, pos, state, entityIn);
        if (definedFluid == FluidsTFC.HOT_WATER.get() && entityIn instanceof EntityLivingBase)
        {
            EntityLivingBase entityLiving = (EntityLivingBase) entityIn;
            if (Constants.RNG.nextInt(10) == 0 && entityLiving.getHealth() < entityLiving.getMaxHealth())
            {
                entityLiving.heal(FoodStatsTFC.PASSIVE_HEAL_AMOUNT * 3.5f);
            }
        }
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
