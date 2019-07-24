/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.objects.te.TELargeVessel;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
public class BlockLargeVessel extends Block implements IItemSize
{
    public static final PropertyBool SEALED = PropertyBool.create("sealed");
    private static final AxisAlignedBB BOUNDING_BOX = new AxisAlignedBB(0.1875D, 0.0D, 0.1875D, 0.8125D, 0.625D, 0.8125D);
    private static final AxisAlignedBB BOUNDING_BOX_SEALED = new AxisAlignedBB(0.15625D, 0.0D, 0.15625D, 0.84375D, 0.6875D, 0.84375D);

    public BlockLargeVessel()
    {
        super(Material.CIRCUITS);
        setSoundType(SoundType.STONE);
        setHardness(2F);

        setDefaultState(blockState.getBaseState().withProperty(SEALED, false));
    }

    @Override
    @Nonnull
    public Size getSize(ItemStack stack)
    {
        return Size.HUGE;
    }

    @Override
    @Nonnull
    public Weight getWeight(ItemStack stack)
    {
        return Weight.HEAVY;
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
        return state.getValue(SEALED) ? BOUNDING_BOX_SEALED : BOUNDING_BOX;
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
            TELargeVessel te = Helpers.getTE(worldIn, pos, TELargeVessel.class);

            if (te != null)
            {
                if (heldItem.isEmpty() && playerIn.isSneaking())
                {
                    worldIn.playSound(null, pos, SoundEvents.BLOCK_WOOD_PLACE, SoundCategory.BLOCKS, 1.0F, 0.85F);
                    worldIn.setBlockState(pos, state.withProperty(SEALED, !state.getValue(SEALED)));
                    te.onSealed();
                }
                else
                {
                    TFCGuiHandler.openGui(worldIn, pos, playerIn, TFCGuiHandler.Type.LARGE_VESSEL);
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


        NBTTagCompound compound = stack.getTagCompound();

        if (compound != null)
        {
            TELargeVessel te = Helpers.getTE(worldIn, pos, TELargeVessel.class);

            if (te != null)
            {
                te.readFromItemTag(compound);
                worldIn.setBlockState(pos, state.withProperty(SEALED, true));
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
        return new TELargeVessel();
    }

    @Override
    public void getDrops(NonNullList<ItemStack> drops, IBlockAccess world, BlockPos pos, IBlockState state, int fortune)
    {
        TELargeVessel te = Helpers.getTE(world, pos, TELargeVessel.class);

        if (te != null)
        {
            if (state.getValue(SEALED))
            {
                ItemStack stack = new ItemStack(Item.getItemFromBlock(this), 1);
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
