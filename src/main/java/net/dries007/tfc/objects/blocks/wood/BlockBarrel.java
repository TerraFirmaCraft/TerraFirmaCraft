/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.wood;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.properties.PropertyBool;
import net.minecraft.block.state.BlockStateContainer;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.*;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.fluids.FluidUtil;
import net.minecraftforge.fluids.capability.CapabilityFluidHandler;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.te.TEBarrel;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
public class BlockBarrel extends Block
{
    public static final PropertyBool SEALED = PropertyBool.create("sealed");
    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.125D, 0.0D, 0.125D, 0.875D, 1.0D, 0.875D);

    public BlockBarrel()
    {
        super(Material.WOOD);
        setSoundType(SoundType.WOOD);
        setHardness(2F);

        setDefaultState(blockState.getBaseState().withProperty(SEALED, false));
    }

    @Override
    @Nonnull
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

    @Override
    @SuppressWarnings("deprecation")
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @Nonnull
    @SuppressWarnings("deprecation")
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        return BOUNDING_BOX;
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    @Nonnull
    @SideOnly(Side.CLIENT)
    public BlockRenderLayer getRenderLayer()
    {
        return BlockRenderLayer.CUTOUT;
    }

    @Override
    public boolean onBlockActivated(World worldIn, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (!worldIn.isRemote)
        {
            ItemStack heldItem = playerIn.getHeldItem(hand);
            TEBarrel te = Helpers.getTE(worldIn, pos, TEBarrel.class);

            if (te != null)
            {
                if (heldItem.isEmpty() && playerIn.isSneaking())
                {
                    worldIn.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 0.85F);
                    worldIn.setBlockState(pos, state.withProperty(SEALED, !state.getValue(SEALED)));
                    te.onSealed();
                }
                else if (heldItem.hasCapability(CapabilityFluidHandler.FLUID_HANDLER_ITEM_CAPABILITY, null))
                {
                    if (!state.getValue(SEALED))
                    {
                        IFluidHandler fluidHandler = te.getCapability(CapabilityFluidHandler.FLUID_HANDLER_CAPABILITY, null);
                        if (fluidHandler != null)
                        {
                            FluidUtil.interactWithFluidHandler(playerIn, hand, fluidHandler);
                            te.markDirty();
                        }
                    }
                }
                else
                {
                    TFCGuiHandler.openGui(worldIn, pos, playerIn, TFCGuiHandler.Type.BARREL);
                }
            }

            return true;
        }
        else
        {
            return true;
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
                TEBarrel te = Helpers.getTE(worldIn, pos, TEBarrel.class);

                if (te != null)
                {
                    te.readFromItemTag(compound);
                    //worldIn.notifyBlockUpdate(pos, state, state, 3);
                }
            }
        }
    }

    @Override
    @Nonnull
    public BlockStateContainer createBlockState()
    {
        return new BlockStateContainer(this, SEALED);
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
    public boolean hasTileEntity(IBlockState state)
    {
        return true;
    }

    @Nullable
    @Override
    public TileEntity createTileEntity(World world, IBlockState state)
    {
        return new TEBarrel();
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        TEBarrel te = Helpers.getTE(world, pos, TEBarrel.class);

        if (te != null)
        {
            if (state.getValue(SEALED))
            {
                ItemStack stack = new ItemStack(Item.getItemFromBlock(this), 1, 1);
                stack.setTagCompound(te.getItemTag());

                drops.add(stack);
            }
            else
            {
                drops.add(new ItemStack(Item.getItemFromBlock(this)));

                IItemHandler inventory = te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);

                if (inventory != null)
                {
                    for (int slot = 0; slot < inventory.getSlots(); slot++)
                    {
                        ItemStack stack = inventory.getStackInSlot(slot);

                        if (!stack.isEmpty())
                        {
                            drops.add(stack);
                        }
                    }
                }
            }
        }
    }
}
