/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.blocks.metal;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
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
import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.objects.items.metal.ItemMetal;
import net.dries007.tfc.objects.te.TEIngotPile;
import net.dries007.tfc.util.Helpers;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class BlockIngotPile extends Block implements ITileEntityProvider
{

    public BlockIngotPile()
    {
        super(Material.IRON);

        TileEntity.register(TEIngotPile.ID.toString(), TEIngotPile.class);
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TEIngotPile();
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    /*@Override
    @SuppressWarnings("deprecation")
    public EnumBlockRenderType getRenderType(IBlockState state)
    {
        return EnumBlockRenderType.ENTITYBLOCK_ANIMATED;
    }

    @Override
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        return BlockFaceShape.UNDEFINED;
    }*/

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getCollisionBoundingBox(IBlockState blockState, IBlockAccess worldIn, BlockPos pos)
    {
        TEIngotPile te = Helpers.getTE(worldIn, pos, TEIngotPile.class);
        double y = te != null ? 0.0625d * (te.getCount() / 8) : 1;
        return new AxisAlignedBB(0d, 0d, 0d, 1d, y, 1d);
    }

    @SideOnly(Side.CLIENT)
    @SuppressWarnings("deprecation")
    @Override
    public AxisAlignedBB getSelectedBoundingBox(IBlockState state, World worldIn, BlockPos pos)
    {
        TEIngotPile te = Helpers.getTE(worldIn, pos, TEIngotPile.class);
        int y = te != null ? te.getCount() >> 3 : 1;
        return new AxisAlignedBB(0d, 0d, 0d, 1d, y, 1d);
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        TEIngotPile te = Helpers.getTE(worldIn, pos, TEIngotPile.class);
        if (te != null)
        {
            if (!playerIn.isSneaking())
            {
                te.setCount(te.getCount() - 1);
                if (!worldIn.isRemote)
                {
                    if (te.getCount() <= 0)
                    {
                        worldIn.setBlockState(pos, Blocks.AIR.getDefaultState());
                    }
                    InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY() + 0.0625 * (te.getCount() / 8), pos.getZ(), new ItemStack(ItemMetal.get(te.getMetal(), Metal.ItemType.INGOT)));
                }
            }
            else
            {
                if (playerIn.getHeldItem(hand).getItem() instanceof ItemMetal)
                {
                    //noinspection ConstantConditions
                    ItemMetal item = (ItemMetal) playerIn.getHeldItem(hand).getItem();
                    if (item.type == Metal.ItemType.INGOT && item.metal == te.getMetal())
                    {
                        if (te.getCount() < 64)
                        {
                            te.setCount(te.getCount() + 1);
                            playerIn.setHeldItem(hand, Helpers.consumeItem(playerIn.getHeldItem(hand), playerIn, 1));
                        }
                    }
                }
            }
        }
        return true;
    }

    @Override
    public ItemStack getPickBlock(IBlockState state, RayTraceResult target, World world, BlockPos pos, EntityPlayer player)
    {
        TEIngotPile te = Helpers.getTE(world, pos, TEIngotPile.class);
        return new ItemStack(ItemMetal.get((te != null ? te.getMetal() : Metal.UNKNOWN), Metal.ItemType.INGOT));
    }
}
