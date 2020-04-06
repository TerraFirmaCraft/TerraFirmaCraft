/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.te;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.block.Block;
import net.minecraft.block.BlockChest;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.InventoryLargeChest;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.CapabilityItemHandler;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.api.capability.size.CapabilityItemSize;
import net.dries007.tfc.api.capability.size.IItemSize;
import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.blocks.wood.BlockChestTFC;
import net.dries007.tfc.objects.container.ContainerChestTFC;
import net.dries007.tfc.objects.inventory.capability.ISlotCallback;
import net.dries007.tfc.objects.inventory.capability.TFCDoubleChestItemHandler;

@ParametersAreNonnullByDefault
@MethodsReturnNonnullByDefault
public class TEChestTFC extends TileEntityChest implements ISlotCallback
{
    public static final int SIZE = 18;

    private Tree cachedWood;
    private int shadowTicksSinceSync;

    {
        chestContents = NonNullList.withSize(SIZE, ItemStack.EMPTY);
        shadowTicksSinceSync = 0;
    }

    @Nullable
    public Tree getWood()
    {
        if (cachedWood == null)
        {
            if (world != null)
            {
                cachedWood = ((BlockChestTFC) world.getBlockState(pos).getBlock()).wood;
            }
        }
        return cachedWood;
    }

    @Override
    public int getSizeInventory()
    {
        return SIZE;
    }

    @Override
    protected boolean isChestAt(@Nonnull BlockPos posIn)
    {
        if (world == null) return false;

        Block block = this.world.getBlockState(posIn).getBlock();
        return block instanceof BlockChestTFC && ((BlockChestTFC) block).wood == getWood() && ((BlockChest) block).chestType == getChestType();
    }

    @Override
    public void update()
    {
        checkForAdjacentChests();
        shadowTicksSinceSync++;

        if (!world.isRemote && numPlayersUsing != 0 && (shadowTicksSinceSync + pos.getX() + pos.getY() + pos.getZ()) % 200 == 0)
        {
            numPlayersUsing = 0;

            for (EntityPlayer player : world.getEntitiesWithinAABB(EntityPlayer.class, new AxisAlignedBB(pos.add(-5, -5, -5), pos.add(6, 6, 6))))
            {
                if (player.openContainer instanceof ContainerChestTFC)
                {
                    IInventory iinventory = ((ContainerChestTFC) player.openContainer).getLowerChestInventory();
                    if (iinventory == this || iinventory instanceof InventoryLargeChest && ((InventoryLargeChest) iinventory).isPartOfLargeChest(this))
                    {
                        ++numPlayersUsing;
                    }
                }
            }
        }

        prevLidAngle = lidAngle;

        if (numPlayersUsing > 0 && lidAngle == 0.0F && adjacentChestZNeg == null && adjacentChestXNeg == null)
        {
            double centerX = pos.getX() + 0.5D;
            double centerZ = pos.getZ() + 0.5D;

            if (adjacentChestZPos != null)
            {
                centerZ += 0.5D;
            }

            if (adjacentChestXPos != null)
            {
                centerX += 0.5D;
            }

            world.playSound(null, centerX, pos.getY() + 0.5D, centerZ, SoundEvents.BLOCK_CHEST_OPEN, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
        }

        if (numPlayersUsing == 0 && lidAngle > 0.0F || numPlayersUsing > 0 && lidAngle < 1.0F)
        {
            float initialAngle = this.lidAngle;
            if (numPlayersUsing > 0)
            {
                lidAngle += 0.1F;
            }
            else
            {
                lidAngle -= 0.1F;
            }

            if (lidAngle > 1.0F)
            {
                lidAngle = 1.0F;
            }

            if (lidAngle < 0.5F && initialAngle >= 0.5F && adjacentChestZNeg == null && adjacentChestXNeg == null)
            {
                double centerX = pos.getX() + 0.5D;
                double centerZ = pos.getZ() + 0.5D;

                if (adjacentChestZPos != null)
                {
                    centerZ += 0.5D;
                }

                if (adjacentChestXPos != null)
                {
                    centerX += 0.5D;
                }

                world.playSound(null, centerX, pos.getY() + 0.5D, centerZ, SoundEvents.BLOCK_CHEST_CLOSE, SoundCategory.BLOCKS, 0.5F, world.rand.nextFloat() * 0.1F + 0.9F);
            }

            if (lidAngle < 0.0F)
            {
                lidAngle = 0.0F;
            }
        }
    }

    @SuppressWarnings("unchecked")
    @Override
    @Nullable
    public <T> T getCapability(net.minecraftforge.common.capabilities.Capability<T> capability, @Nullable net.minecraft.util.EnumFacing facing)
    {
        if (capability == CapabilityItemHandler.ITEM_HANDLER_CAPABILITY)
        {
            if (doubleChestHandler == null || doubleChestHandler.needsRefresh())
            {
                doubleChestHandler = TFCDoubleChestItemHandler.get(this);
            }
            if (doubleChestHandler != null && doubleChestHandler != TFCDoubleChestItemHandler.NO_ADJACENT_CHESTS_INSTANCE)
            {
                return (T) doubleChestHandler;
            }
        }
        return super.getCapability(capability, facing);
    }

    @Override
    public boolean shouldRefresh(World world, BlockPos pos, IBlockState oldState, IBlockState newSate)
    {
        return oldState.getBlock() != newSate.getBlock();
    }

    @Override
    @SideOnly(Side.CLIENT)
    @Nonnull
    public AxisAlignedBB getRenderBoundingBox()
    {
        return new AxisAlignedBB(getPos().add(-1, 0, -1), getPos().add(2, 2, 2));
    }

    @Override
    public boolean isItemValidForSlot(int index, ItemStack stack)
    {
        // Blocks input from hopper
        IItemSize cap = CapabilityItemSize.getIItemSize(stack);
        if (cap != null)
        {
            return cap.getSize(stack).isSmallerThan(Size.VERY_LARGE);
        }
        return true;
    }

    @Override
    public boolean isItemValid(int slot, @Nonnull ItemStack stack)
    {
        return isItemValidForSlot(slot, stack);
    }
}
