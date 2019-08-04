/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks.devices;

import java.util.List;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.SoundType;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.BlockFaceShape;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.objects.items.ItemHandstone;
import net.dries007.tfc.objects.te.TEQuern;
import net.dries007.tfc.util.Helpers;

@ParametersAreNonnullByDefault
public class BlockQuern extends Block implements IItemSize
{
    private static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.625D, 1D);
    private static final AxisAlignedBB QUERN_AABB = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.875D, 1D);
    private static final AxisAlignedBB HANDSTONE_AABB = new AxisAlignedBB(0.375D, 0.625D, 0.375D, 0.625D, 0.875D, 0.625D);

    public BlockQuern()
    {
        super(Material.ROCK);
        setHardness(3.0f);
        setSoundType(SoundType.STONE);
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

    @SuppressWarnings("deprecation")
    @Override
    public boolean isTopSolid(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullBlock(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isBlockNormalCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isNormalCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isFullCube(IBlockState state)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    @Nonnull
    public AxisAlignedBB getBoundingBox(IBlockState state, IBlockAccess source, BlockPos pos)
    {
        TEQuern teQuern = Helpers.getTE(source, pos, TEQuern.class);
        if (teQuern != null && teQuern.hasHandstone())
        {
            return QUERN_AABB;
        }
        else
        {
            return BASE_AABB;
        }
    }

    @Override
    @Nonnull
    @SuppressWarnings("deprecation")
    public BlockFaceShape getBlockFaceShape(IBlockAccess worldIn, IBlockState state, BlockPos pos, EnumFacing face)
    {
        if (face == EnumFacing.DOWN)
        {
            return BlockFaceShape.SOLID;
        }
        return BlockFaceShape.UNDEFINED;
    }

    @SuppressWarnings("deprecation")
    @Override
    public void addCollisionBoxToList(IBlockState state, World world, BlockPos pos, AxisAlignedBB entityBox, List<AxisAlignedBB> collidingBoxes, @Nullable Entity entityIn, boolean isActualState)
    {
        addCollisionBoxToList(pos, entityBox, collidingBoxes, BASE_AABB);
        TEQuern teQuern = Helpers.getTE(world, pos, TEQuern.class);
        if (teQuern != null && teQuern.hasHandstone())
        {
            addCollisionBoxToList(pos, entityBox, collidingBoxes, HANDSTONE_AABB);
        }
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isOpaqueCube(IBlockState state)
    {
        return false;
    }

    @Override
    public void breakBlock(World world, BlockPos pos, IBlockState state)
    {
        TEQuern teQuern = Helpers.getTE(world, pos, TEQuern.class);
        if (teQuern != null)
        {
            teQuern.onBreakBlock(world, pos);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (hand.equals(EnumHand.MAIN_HAND))
        {
            ItemStack playerStack = playerIn.getHeldItem(EnumHand.MAIN_HAND);

            TEQuern teQuern = Helpers.getTE(world, pos, TEQuern.class);
            if (teQuern != null)
            {
                boolean isGrinding = teQuern.isGrinding();
                if (!isGrinding && !playerIn.isSneaking())
                {
                    if (playerStack.isEmpty() && facing == EnumFacing.UP && hitX > 0.2f && hitX < 0.4f && hitZ > 0.2f && hitZ < 0.4f && hitY >= 0.875)
                    {
                        teQuern.grind();
                        world.playSound(null, pos, TFCSounds.QUERN_USE, SoundCategory.BLOCKS, 1, 1 + ((world.rand.nextFloat() - world.rand.nextFloat()) / 16));
                    }
                    else if (!world.isRemote)
                    {
                        TFCGuiHandler.openGui(world, pos, playerIn, TFCGuiHandler.Type.QUERN);
                    }
                    return true;
                }
                else if (!world.isRemote && playerIn.isSneaking())
                {
                    IItemHandler inventory = teQuern.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                    if (inventory != null)
                    {
                        ItemStack inputStack = inventory.getStackInSlot(TEQuern.SLOT_INPUT);
                        ItemStack outputStack = inventory.getStackInSlot(TEQuern.SLOT_OUTPUT);

                        if (hitX > 0.2f && hitX < 0.8f && hitZ > 0.2f && hitZ < 0.8f)
                        {
                            if (hitX > 0.4f && hitX < 0.6f && hitZ > 0.4f && hitZ < 0.6f && playerStack.isEmpty() && !inputStack.isEmpty())
                            {
                                playerIn.setHeldItem(EnumHand.MAIN_HAND, inventory.extractItem(TEQuern.SLOT_INPUT, inputStack.getCount(), false));
                                teQuern.setAndUpdateSlots(TEQuern.SLOT_INPUT);
                                world.playSound(null, pos, SoundEvents.BLOCK_CLOTH_HIT, SoundCategory.BLOCKS, 1, 1);
                            }
                            else if (!isGrinding && playerStack.isEmpty() && teQuern.hasHandstone())
                            {
                                playerIn.setHeldItem(EnumHand.MAIN_HAND, inventory.extractItem(TEQuern.SLOT_HANDSTONE, inventory.getStackInSlot(TEQuern.SLOT_HANDSTONE).getCount(), false));
                                teQuern.setAndUpdateSlots(TEQuern.SLOT_HANDSTONE);
                                world.playSound(null, pos, SoundEvents.BLOCK_STONE_HIT, SoundCategory.BLOCKS, 1, 1);
                            }
                            else if (!isGrinding && !playerStack.isEmpty() && teQuern.isItemValid(TEQuern.SLOT_HANDSTONE, playerStack))
                            {
                                if (playerStack.getCount() == 1)
                                    playerIn.setHeldItem(EnumHand.MAIN_HAND, teQuern.insertOrSwapItem(TEQuern.SLOT_HANDSTONE, playerStack));
                                else
                                    playerIn.addItemStackToInventory(teQuern.insertOrSwapItem(TEQuern.SLOT_HANDSTONE, playerStack.splitStack(1)));
                                teQuern.setAndUpdateSlots(TEQuern.SLOT_HANDSTONE);
                                world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1, 1);
                            }
                        }
                        else if ((hitX <= 0.2f || hitX >= 0.8f || hitZ <= 0.2f || hitZ >= 0.8f) && hitY >= 0.625)
                        {
                            if (playerStack.isEmpty() && !outputStack.isEmpty())
                            {
                                playerIn.setHeldItem(EnumHand.MAIN_HAND, teQuern.takeCraftingResult(outputStack));
                                teQuern.setAndUpdateSlots(TEQuern.SLOT_OUTPUT);
                                world.playSound(null, pos, SoundEvents.BLOCK_CLOTH_HIT, SoundCategory.BLOCKS, 1, 1);
                            }
                            else if (!playerStack.isEmpty() && !outputStack.isEmpty() && !(playerStack.getItem() instanceof ItemHandstone))
                            {
                                playerIn.addItemStackToInventory(teQuern.takeCraftingResult(outputStack));
                                teQuern.setAndUpdateSlots(TEQuern.SLOT_OUTPUT);
                                world.playSound(null, pos, SoundEvents.BLOCK_CLOTH_PLACE, SoundCategory.BLOCKS, 1, 1);
                            }
                            else if (!isGrinding && playerStack.isEmpty() && teQuern.hasHandstone())
                            {
                                playerIn.setHeldItem(EnumHand.MAIN_HAND, inventory.extractItem(TEQuern.SLOT_HANDSTONE, inventory.getStackInSlot(TEQuern.SLOT_HANDSTONE).getCount(), false));
                                teQuern.setAndUpdateSlots(TEQuern.SLOT_HANDSTONE);
                                world.playSound(null, pos, SoundEvents.BLOCK_STONE_HIT, SoundCategory.BLOCKS, 1, 1);
                            }
                            else if (!isGrinding && !playerStack.isEmpty() && teQuern.isItemValid(TEQuern.SLOT_HANDSTONE, playerStack))
                            {
                                if (playerStack.getCount() == 1)
                                {
                                    playerIn.setHeldItem(EnumHand.MAIN_HAND, teQuern.insertOrSwapItem(TEQuern.SLOT_HANDSTONE, playerStack));
                                }
                                else
                                {
                                    playerIn.addItemStackToInventory(teQuern.insertOrSwapItem(TEQuern.SLOT_HANDSTONE, playerStack.splitStack(1)));
                                }
                                teQuern.setAndUpdateSlots(TEQuern.SLOT_HANDSTONE);
                                world.playSound(null, pos, SoundEvents.BLOCK_STONE_PLACE, SoundCategory.BLOCKS, 1, 1);
                            }
                        }
                        return true;
                    }
                }
            }
        }
        return false;
    }

    @Override
    public boolean isNormalCube(IBlockState state, IBlockAccess world, BlockPos pos)
    {
        return false;
    }

    @SuppressWarnings("deprecation")
    @Override
    public boolean isSideSolid(IBlockState baseState, IBlockAccess world, BlockPos pos, EnumFacing side)
    {
        return side == EnumFacing.DOWN;
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
        return new TEQuern();
    }
}
