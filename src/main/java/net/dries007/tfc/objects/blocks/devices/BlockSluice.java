/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.devices;

import java.util.List;
import java.util.Random;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.Explosion;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;
import net.minecraftforge.fluids.Fluid;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.objects.te.TESluice;
import net.dries007.tfc.util.block.BoundingBox;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockSluice extends BlockHorizontal implements IItemSize
{
    public static final PropertyBool UPPER = PropertyBool.create("upper"); //true if this is the upper half
    // From bottom to top, 1 step (4/16 block) at a time
    // [0=lower half, 1=upper half][step]
    private static final BoundingBox[][] BOXES =
        {
            {
                new BoundingBox(0.5D, 0.0625D, 0.125D, 0.5D, 0.0625D, 0.125D, EnumFacing.SOUTH),
                new BoundingBox(0.5D, 0.125D, 0.375D, 0.5D, 0.125D, 0.125D, EnumFacing.SOUTH),
                new BoundingBox(0.5D, 0.1875D, 0.625D, 0.5D, 0.1875D, 0.125D, EnumFacing.SOUTH),
                new BoundingBox(0.5D, 0.25D, 0.875D, 0.5D, 0.25D, 0.125D, EnumFacing.SOUTH)
            },
            {
                new BoundingBox(0.5D, 0.3125D, 0.125D, 0.5D, 0.3125D, 0.125D, EnumFacing.SOUTH),
                new BoundingBox(0.5D, 0.375D, 0.375D, 0.5D, 0.375D, 0.125D, EnumFacing.SOUTH),
                new BoundingBox(0.5D, 0.4375D, 0.625D, 0.5D, 0.4375D, 0.125D, EnumFacing.SOUTH),
                new BoundingBox(0.5D, 0.5D, 0.875D, 0.5D, 0.5D, 0.125D, EnumFacing.SOUTH)
            }
        };
    private static final AxisAlignedBB LOWER_AABB = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.5D, 1D);

    public BlockSluice()
    {
        super(Material.WOOD);
        setDefaultState(blockState.getBaseState().withProperty(UPPER, false));
        setHardness(8.0f);
        setHarvestLevel("axe", 0);
    }

    @Nonnull
    @Override
    public Size getSize(@Nonnull ItemStack stack)
    {
        return Size.LARGE; // Only in chests
    }

    @Nonnull
    @Override
    public Weight getWeight(@Nonnull ItemStack stack)
    {
        return Weight.VERY_HEAVY; // Stack size = 1
    }

    @SuppressWarnings("deprecation")
    @Override
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(FACING, EnumFacing.byHorizontalIndex(meta)).withProperty(UPPER, meta > 3);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return state.getValue(FACING).getHorizontalIndex() + (state.getValue(UPPER) ? 4 : 0);
    }

    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        if (state.getValue(UPPER))
        {
            return FULL_BLOCK_AABB;
        }
        else
        {
            return LOWER_AABB;
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        EnumFacing facing = world.getBlockState(pos).getValue(FACING);
        boolean upper = world.getBlockState(pos).getValue(UPPER);

        BoundingBox[] part = BOXES[upper ? 1 : 0];

        for (int i = 0; i < 4; i++)
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, part[i].getAABB(facing));
        }
    }

    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public void onPlayerDestroy(World worldIn, BlockPos pos, IBlockState state)
    {
        BlockPos fluidPos = pos.offset(state.getValue(FACING), -1).down();
        Block block = state.getBlock();
        if (block instanceof BlockFluidBase)
        {
            Fluid fluid = ((BlockFluidBase) block).getFluid();
            if (TESluice.isValidFluid(fluid))
            {
                worldIn.setBlockToAir(fluidPos);
            }
        }
        super.onPlayerDestroy(worldIn, pos, state);
    }

    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        EnumFacing enumfacing = state.getValue(FACING);

        if (!state.getValue(UPPER))
        {
            if (worldIn.getBlockState(pos.offset(enumfacing)).getBlock() != this)
            {
                worldIn.setBlockToAir(pos);
            }
        }
        else if (worldIn.getBlockState(pos.offset(enumfacing.getOpposite())).getBlock() != this)
        {
            if (!worldIn.isRemote)
            {
                spawnAsEntity(worldIn, pos, new ItemStack(this));
            }
            worldIn.setBlockToAir(pos);
        }

        //Keep flowing liquids from reaching the top of this block
        IBlockState blockState = worldIn.getBlockState(pos.up());
        if (blockState.getBlock() instanceof BlockFluidBase && blockState.getValue(BlockFluidBase.LEVEL) < 15)
        {
            worldIn.setBlockToAir(pos.up());
        }
    }

    @Override
    public Item getItemDropped(IBlockState state, Random rand, int fortune)
    {
        return state.getValue(UPPER) ? Item.getItemFromBlock(this) : Items.AIR;
    }

    @Override
    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        if (state.getValue(UPPER))
        {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        }
    }

    @Override
    public void onExplosionDestroy(World worldIn, BlockPos pos, Explosion explosionIn)
    {
        IBlockState state = worldIn.getBlockState(pos);
        BlockPos fluidPos = pos.offset(state.getValue(FACING), -1).down();
        Block block = state.getBlock();
        if (block instanceof BlockFluidBase)
        {
            Fluid fluid = ((BlockFluidBase) block).getFluid();
            if (TESluice.isValidFluid(fluid))
            {
                worldIn.setBlockToAir(fluidPos);
            }
        }
        super.onExplosionDestroy(worldIn, pos, explosionIn);
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FACING, UPPER);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return state.getValue(UPPER);
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return state.getValue(UPPER) ? new TESluice() : null;
    }
}
