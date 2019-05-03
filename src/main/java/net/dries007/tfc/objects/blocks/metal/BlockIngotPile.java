/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.metal;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.blocks.BlocksTFC;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import net.dries007.tfc.objects.te.TEIngotPile;
import net.dries007.tfc.objects.te.TEWorldItem;
import net.dries007.tfc.util.Helpers;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockIngotPile extends Block
{
    public BlockIngotPile()
    {
        super(Material.IRON);

        setHardness(3.0F);
        setResistance(10.0F);
        setHarvestLevel("pickaxe", 0);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        TEIngotPile te = Helpers.getTE(source, pos, TEIngotPile.class);
        double y = te != null ? 0.125 * (te.getCount() / 8.0) : 1;
        return new AxisAlignedBB(0d, 0d, 0d, 1d, y, 1d);
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        TEIngotPile te = Helpers.getTE(worldIn, pos, TEIngotPile.class);
        double y = te != null ? 0.125 * (te.getCount() / 8.0) : 1;
        return new AxisAlignedBB(0d, 0d, 0d, 1d, y, 1d);
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    {
        TEIngotPile te = Helpers.getTE(worldIn, pos, TEIngotPile.class);
        double y = te != null ? 0.125 * (te.getCount() / 8.0) : 1;
        return new AxisAlignedBB(0d, 0d, 0d, 1d, y, 1d);
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
        if (!collapseDown(worldIn, pos) && !worldIn.isSideSolid(pos.down(), EnumFacing.UP))
        {
            worldIn.setBlockToAir(pos);
        }
    }

    @Override
    public void breakBlock(World worldIn, BlockPos pos, IBlockState state)
    {
        TEIngotPile te = Helpers.getTE(worldIn, pos, TEIngotPile.class);
        if (te != null) te.onBreakBlock();
        super.breakBlock(worldIn, pos, state);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (playerIn.isSneaking()) return true;

        TEIngotPile te = Helpers.getTE(worldIn, pos, TEIngotPile.class);
        if (te == null) return true;

        BlockPos posTop = pos;
        IBlockState stateTop;
        do
        {
            posTop = posTop.up();
            stateTop = worldIn.getBlockState(posTop);
            if (stateTop.getBlock() != BlocksTFC.INGOT_PILE)
            {
                te.setCount(te.getCount() - 1);
                if (!worldIn.isRemote)
                {
                    if (te.getCount() <= 0)
                    {
                        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                    playerIn.addItemStackToInventory(new ItemStack(ItemMetal.get(te.getMetal(), Metal.ItemType.INGOT)));
                }
                return true;
            }
            else
            {
                te = Helpers.getTE(worldIn, posTop, TEIngotPile.class);
                if (te != null)
                {
                    if (te.getCount() < 64)
                    {
                        te.setCount(te.getCount() - 1);
                        if (!worldIn.isRemote)
                        {
                            if (te.getCount() <= 0)
                            {
                                worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
                            }
                            playerIn.addItemStackToInventory(new ItemStack(ItemMetal.get(te.getMetal(), Metal.ItemType.INGOT)));
                        }
                        return true;
                    }
                }
            }
        } while (posTop.getY() <= 256);
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isSideSolid(IBlockState base_state, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        TEIngotPile te = Helpers.getTE(world, pos, TEIngotPile.class);
        if (te == null) return false;
        return te.getCount() == 64;
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
        return new TEWorldItem();
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        TEIngotPile te = Helpers.getTE(world, pos, TEIngotPile.class);
        if (te != null)
        {
            return new ItemStack(ItemMetal.get(te.getMetal(), Metal.ItemType.INGOT));
        }
        return ItemStack.EMPTY;
    }

    private boolean collapseDown(World world, BlockPos pos)
    {
        IBlockState stateDown = world.getBlockState(pos.down());
        if (stateDown.getBlock() == BlocksTFC.INGOT_PILE)
        {

            TEIngotPile te = Helpers.getTE(world, pos.down(), TEIngotPile.class);
            TEIngotPile teUp = Helpers.getTE(world, pos, TEIngotPile.class);
            if (te != null && teUp != null && te.getCount() < 64)
            {
                if (te.getCount() + teUp.getCount() <= 64)
                {
                    te.setCount(te.getCount() + teUp.getCount());
                    world.setBlockToAir(pos);
                }
                else
                {
                    te.setCount(64);
                    teUp.setCount(te.getCount() + teUp.getCount() - 64);
                }
            }
            return true;
        }
        return false;
    }
}
