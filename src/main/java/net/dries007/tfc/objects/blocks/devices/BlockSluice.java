/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.devices;

import java.util.Random;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fluids.BlockFluidBase;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.te.TESluice;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockSluice extends BlockHorizontal
{
    public static final PropertyBool UPPER = PropertyBool.create("upper"); //true if this is the upper half

    public BlockSluice()
    {
        super(Material.WOOD);
        setDefaultState(blockState.getBaseState().withProperty(UPPER, false));
        setHardness(8.0f);
        setHarvestLevel("axe", 0);
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
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
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

    public void dropBlockAsItemWithChance(World worldIn, BlockPos pos, IBlockState state, float chance, int fortune)
    {
        if (state.getValue(UPPER))
        {
            super.dropBlockAsItemWithChance(worldIn, pos, state, chance, fortune);
        }
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
