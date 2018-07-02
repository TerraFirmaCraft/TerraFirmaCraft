/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 *
 */

package net.dries007.tfc.objects.blocks;

import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.InventoryHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;

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

    @Override
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
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
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
                    InventoryHelper.spawnItemStack(worldIn, pos.getX(), pos.getY(), pos.getZ(), new ItemStack(ItemMetal.get(te.getMetal(), Metal.ItemType.INGOT)));
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
                            playerIn.setHeldItem(hand, Helpers.consumeItem(playerIn.getHeldItem(hand), 1));
                        }
                    }
                }
            }
        }
        return true;
    }


}
