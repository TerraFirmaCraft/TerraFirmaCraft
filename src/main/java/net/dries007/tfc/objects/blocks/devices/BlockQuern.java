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
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemHandlerHelper;

import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;
import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.client.gui.overlay.IHighlightHandler;
import net.dries007.tfc.objects.te.TEQuern;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.objects.te.TEQuern.SLOT_HANDSTONE;

@ParametersAreNonnullByDefault
public class BlockQuern extends Block implements IItemSize, IHighlightHandler
{
    private static final AxisAlignedBB BASE_AABB = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.625D, 1D);
    private static final AxisAlignedBB QUERN_AABB = new AxisAlignedBB(0D, 0D, 0D, 1D, 0.875D, 1D);

    private static final AxisAlignedBB HANDSTONE_AABB = new AxisAlignedBB(0.1875D, 0.625D, 0.1875D, 0.8125D, 0.86D, 0.8125D);
    private static final AxisAlignedBB HANDLE_AABB = new AxisAlignedBB(0.27125D, 0.86D, 0.27125D, 0.335D, 1.015D, 0.335D);

    private static final AxisAlignedBB INPUT_SLOT_AABB = new AxisAlignedBB(0.375D, 0.86D, 0.375D, 0.625D, 1.015D, 0.625D);

    /**
     * Gets the selection place player is looking at
     * Used for interaction / selection box drawing
     */
    private static SelectionPlace getPlayerSelection(World world, BlockPos pos, EntityPlayer player)
    {
        // This will compute a line from the camera center (crosshair) starting at the player eye pos and a little after this block
        // so we can grab the exact point regardless from which face player is looking from
        double length = Math.sqrt(pos.distanceSqToCenter(player.posX, player.posY, player.posZ)) + 1.5D;
        Vec3d eyePos = new Vec3d(player.posX, player.posY + player.getEyeHeight(), player.posZ);
        Vec3d lookingPos = eyePos.add(new Vec3d(player.getLookVec().x * length, player.getLookVec().y * length, player.getLookVec().z * length));

        TEQuern teQuern = Helpers.getTE(world, pos, TEQuern.class);

        if (teQuern != null)
        {
            IItemHandler inventory = teQuern.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
            // Draws the correct selection box depending on where the player is looking at
            if (!teQuern.isGrinding() && teQuern.hasHandstone() && HANDLE_AABB.offset(pos).calculateIntercept(eyePos, lookingPos) != null)
            {
                return SelectionPlace.HANDLE;
            }
            else if (!teQuern.isGrinding() && teQuern.hasHandstone() && (!player.getHeldItem(EnumHand.MAIN_HAND).isEmpty() || (inventory != null && !inventory.getStackInSlot(TEQuern.SLOT_INPUT).isEmpty())) && INPUT_SLOT_AABB.offset(pos).calculateIntercept(eyePos, lookingPos) != null)
            {
                return SelectionPlace.INPUT_SLOT;
            }
            else if ((teQuern.hasHandstone() || teQuern.isItemValid(TEQuern.SLOT_HANDSTONE, player.getHeldItem(EnumHand.MAIN_HAND))) && HANDSTONE_AABB.offset(pos).calculateIntercept(eyePos, lookingPos) != null)
            {
                return SelectionPlace.HANDSTONE;
            }
        }
        return SelectionPlace.BASE;
    }

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
        return Size.VERY_LARGE; // Can't store anywhere, but don't overburden
    }

    @Override
    @Nonnull
    public Weight getWeight(ItemStack stack)
    {
        return Weight.VERY_HEAVY; // Stacksize = 1
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
            teQuern.onBreakBlock(world, pos, state);
        }
        super.breakBlock(world, pos, state);
    }

    @Override
    public boolean onBlockActivated(World world, BlockPos pos, IBlockState state, EntityPlayer playerIn, EnumHand hand, EnumFacing facing, float hitX, float hitY, float hitZ)
    {
        if (hand.equals(EnumHand.MAIN_HAND))
        {
            TEQuern teQuern = Helpers.getTE(world, pos, TEQuern.class);
            if (teQuern != null && !teQuern.isGrinding())
            {
                ItemStack heldStack = playerIn.getHeldItem(hand);
                SelectionPlace selection = getPlayerSelection(world, pos, playerIn);
                IItemHandler inventory = teQuern.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, null);
                if (inventory != null)
                {
                    if (selection == SelectionPlace.HANDLE)
                    {
                        teQuern.grind();
                        world.playSound(null, pos, TFCSounds.QUERN_USE, SoundCategory.BLOCKS, 1, 1 + ((world.rand.nextFloat() - world.rand.nextFloat()) / 16));
                        return true;
                    }
                    else if (selection == SelectionPlace.INPUT_SLOT)
                    {
                        playerIn.setHeldItem(EnumHand.MAIN_HAND, teQuern.insertOrSwapItem(TEQuern.SLOT_INPUT, heldStack));
                        teQuern.setAndUpdateSlots(TEQuern.SLOT_INPUT);
                        return true;
                    }
                    else if (selection == SelectionPlace.HANDSTONE && inventory.getStackInSlot(SLOT_HANDSTONE).isEmpty() && inventory.isItemValid(SLOT_HANDSTONE, heldStack))
                    {
                        playerIn.setHeldItem(EnumHand.MAIN_HAND, teQuern.insertOrSwapItem(SLOT_HANDSTONE, heldStack));
                        teQuern.setAndUpdateSlots(SLOT_HANDSTONE);
                        return true;
                    }
                    else if (selection == SelectionPlace.BASE && !inventory.getStackInSlot(TEQuern.SLOT_OUTPUT).isEmpty())
                    {
                        ItemHandlerHelper.giveItemToPlayer(playerIn, inventory.extractItem(TEQuern.SLOT_OUTPUT, inventory.getStackInSlot(TEQuern.SLOT_OUTPUT).getCount(), false));
                        teQuern.setAndUpdateSlots(TEQuern.SLOT_OUTPUT);
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

    @Override
    public boolean drawHighlight(World world, BlockPos pos, EntityPlayer player, RayTraceResult rayTrace, double partialTicks)
    {
        double dx = player.lastTickPosX + (player.posX - player.lastTickPosX) * partialTicks;
        double dy = player.lastTickPosY + (player.posY - player.lastTickPosY) * partialTicks;
        double dz = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * partialTicks;

        SelectionPlace selection = getPlayerSelection(world, pos, player);

        // Draws the correct selection box depending on where the player is looking at
        if (selection == SelectionPlace.HANDLE)
        {
            // Draws handle AABB if player is looking at it
            IHighlightHandler.drawBox(HANDLE_AABB.offset(pos).offset(-dx, -dy, -dz), 1f, 0, 0, 0, 0.4f);
        }
        else if (selection == SelectionPlace.INPUT_SLOT)
        {
            // Draws item input AABB if user has item in main hand or there is an item in slot
            IHighlightHandler.drawBox(INPUT_SLOT_AABB.offset(pos).offset(-dx, -dy, -dz), 1f, 0, 0, 0, 0.4f);
        }
        else if (selection == SelectionPlace.HANDSTONE)
        {
            // Draws handstone AABB if player is looking at it
            IHighlightHandler.drawBox(HANDSTONE_AABB.offset(pos).offset(-dx, -dy, -dz).grow(0.002D), 1f, 0, 0, 0, 0.4f);
        }
        else
        {
            // Just draw the base outline (last grow is just what MC does to actually make the outline visible
            IHighlightHandler.drawBox(BASE_AABB.offset(pos).offset(-dx, -dy, -dz).grow(0.002D), 1f, 0, 0, 0, 0.4f);
        }
        return true;
    }

    /**
     * Just a helper enum to figure out where player is looking at
     * Used to draw selection boxes + handle interaction
     */
    private enum SelectionPlace
    {
        HANDLE,
        HANDSTONE,
        INPUT_SLOT,
        BASE
    }
}
