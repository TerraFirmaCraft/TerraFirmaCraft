/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.wood;

import javax.annotation.Nullable;

import net.minecraft.block.Block;
import net.minecraft.block.ITileEntityProvider;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.te.TEBarrel;

public class BlockBarrel extends Block implements ITileEntityProvider
{
    public static final PropertyBool SEALED = PropertyBool.create("sealed");
    private static final AxisAlignedBB bounds = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);

    public BlockBarrel()
    {
        super(Material.WOOD);
        setSoundType(SoundType.WOOD);

        setDefaultState(this.blockState.getBaseState().withProperty(SEALED, false));
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return bounds;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (worldIn.isRemote)
        {
            return true;
        }

        ItemStack heldItem = playerIn.getHeldItem(hand);
        TEBarrel te = (TEBarrel) worldIn.getTileEntity(pos);

        if (!heldItem.isEmpty())
        {
            if (!state.getValue(SEALED))
            {
                FluidUtil.interactWithFluidHandler(playerIn, hand, te.tank);
                te.markDirty();
                worldIn.notifyBlockUpdate(pos, state, state, 3);
            }
        }
        else if (playerIn.isSneaking())
        {
            worldIn.setBlockState(pos, state.withProperty(SEALED, !state.getValue(SEALED)));
        }

        return true;
    }

    @Nullable
    @Override
    public TileEntity createNewTileEntity(World worldIn, int meta)
    {
        return new TEBarrel();
    }

    @Override
    public void onBlockPlacedBy(World worldIn, BlockPos pos, IBlockState state, EntityLivingBase placer, ItemStack stack)
    {
        if (worldIn.isRemote)
        {
            return;
        }

        if (stack.getMetadata() == 1)
        {
            NBTTagCompound compound = stack.getTagCompound();

            if (compound != null)
            {
                TEBarrel te = (TEBarrel)worldIn.getTileEntity(pos);

                te.tank.readFromNBT(compound.getCompoundTag("tank"));
                te.markDirty();
                worldIn.notifyBlockUpdate(pos, state, state, 3);
            }
        }
    }

    @Override
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, SEALED);
    }

    @Override
    @SuppressWarnings("deprecation")
    public IBlockState getStateFromMeta(int meta)
    {
        return this.getDefaultState().withProperty(SEALED, meta == 1);
    }

    @Override
    public int getMetaFromState(IBlockState state)
    {
        if (state.getValue(SEALED))
        {
            return 1;
        }

        return 0;
    }

    /**
     * Prevents removal of the Block & TileEntity before getDrops(...) is called.
     * Using this we'll have to remove the block later, which happens in {@link #harvestBlock(World, EntityPlayer, BlockPos, IBlockState, TileEntity, ItemStack)}.
     */
    @Override
    public boolean removedByPlayer(IBlockState state, World world, BlockPos pos, EntityPlayer player, boolean willHarvest)
    {
        return willHarvest || super.removedByPlayer(state, world, pos, player, false);
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        if (state.getValue(SEALED))
        {
            TEBarrel te = (TEBarrel)world.getTileEntity(pos);
            NBTTagCompound compound = new NBTTagCompound();
            NBTTagCompound tankTag = new NBTTagCompound();

            te.tank.writeToNBT(tankTag);
            compound.setTag("tank", tankTag);

            ItemStack stack = new ItemStack(Item.getItemFromBlock(this), 1, 1);
            stack.setTagCompound(compound);

            drops.add(stack);
        }
        else
        {
            drops.add(new ItemStack(Item.getItemFromBlock(this)));
        }
    }

    /**
     * The Block needs to be removed here since we prevented its removal earlier in {@link #removedByPlayer(IBlockState, World, BlockPos, EntityPlayer, boolean)}.
     */
    @Override
    public void harvestBlock(World world, EntityPlayer player, BlockPos pos, IBlockState state, @Nullable TileEntity te, ItemStack tool)
    {
        super.harvestBlock(world, player, pos, state, te, tool);
        world.setBlockToAir(pos);
    }
}
