/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.devices;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.items.ItemsTFC;
import net.dries007.tfc.objects.te.TEPitKiln;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.objects.blocks.BlockPlacedItem.PLACED_ITEM_AABB;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class BlockPitKiln extends Block
{
    public static final PropertyBool FULL = PropertyBool.create("full");
    public static final PropertyBool LIT = PropertyBool.create("lit");

    public BlockPitKiln()
    {
        super(Material.CIRCUITS);
        setHardness(0.5f);
        setDefaultState(blockState.getBaseState().withProperty(FULL, false).withProperty(LIT, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isTopSolid(IBlockState state)
    {
        // This is required for the fire code, because forge doesn't 'fix' it to use the location sensitive version.
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return getDefaultState();
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        return 0;
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getActualState(IBlockState state, IBlockAccess worldIn, BlockPos pos)
    {
        TEPitKiln te = Helpers.getTE(worldIn, pos, TEPitKiln.class);
        if (te != null)
        {
            return state.withProperty(BlockPitKiln.LIT, te.isLit()).withProperty(BlockPitKiln.FULL, te.hasFuel());
        }
        return state;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return true;
    }

    @Override
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        // todo: depend on fill level?
        return state.getActualState(source, pos).getValue(FULL) ? FULL_BLOCK_AABB : PLACED_ITEM_AABB;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public void neighborChanged(IBlockState state, World worldIn, BlockPos pos, Block blockIn, BlockPos fromPos)
    {
        TEPitKiln te = Helpers.getTE(worldIn, pos, TEPitKiln.class);
        if (te != null)
        {
            if (blockIn == Blocks.FIRE)
            {
                te.tryLight();
            }
            // Make sure the sides are valid
            te.assertValid();
        }
        super.neighborChanged(state, worldIn, pos, blockIn, fromPos);
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TEPitKiln te = Helpers.getTE(worldIn, pos, TEPitKiln.class);
        if (te != null)
        {
            te.onBreakBlock(worldIn, pos);
        }
        super.breakBlock(worldIn, pos, state); // todo: drop items
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TEPitKiln te = Helpers.getTE(worldIn, pos, TEPitKiln.class);
        if (te != null)
        {
            // Skip interacting if using a fire starter
            if (playerIn.getHeldItemMainhand().getItem() == ItemsTFC.FIRESTARTER || playerIn.getHeldItemOffhand().getItem() == ItemsTFC.FIRESTARTER)
            {
                return false;
            }
            return te.onRightClick(playerIn, playerIn.getHeldItem(hand), hitX < 0.5, hitZ < 0.5);
        }
        return false;
    }

    @Override
    protected BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, FULL, LIT);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isSideSolid(IBlockState state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return state.getActualState(world, pos).getValue(FULL);
    }

    @Override
    public boolean isBurning(IBlockAccess world, BlockPos pos)
    {
        return world.getBlockState(pos).getActualState(world, pos).getValue(LIT);
    }

    @Override
    public int getFlammability(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return 0;
    }

    @Override
    public int getFireSpreadSpeed(IBlockAccess world, BlockPos pos, EnumFacing face)
    {
        return world.getBlockState(pos).getActualState(world, pos).getValue(LIT) ? 120 : 0; // Twice as much as the highest vanilla level (60)
    }

    @Override
    public boolean isFireSource(World world, BlockPos pos, EnumFacing side)
    {
        return world.getBlockState(pos).getActualState(world, pos).getValue(LIT);
    }

    @Override
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TEPitKiln();
    }
}