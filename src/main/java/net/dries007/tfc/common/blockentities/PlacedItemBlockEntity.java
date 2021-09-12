/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;


import javax.annotation.Nonnull;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.capabilities.ItemStackHandlerCallback;
import net.dries007.tfc.common.capabilities.size.ItemSizeManager;
import net.dries007.tfc.common.capabilities.size.Size;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class PlacedItemBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public static final int SLOT_LARGE_ITEM = 0;
    private static final Component NAME = new TranslatableComponent(MOD_ID + ".tile_entity.placed_item");
    public boolean isHoldingLargeItem;

    public PlacedItemBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.PLACED_ITEM.get(), pos, state);
    }

    protected PlacedItemBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, self -> new ItemStackHandlerCallback(self, 4), NAME);
        this.isHoldingLargeItem = false;
    }

    public boolean onRightClick(Player player, ItemStack stack, BlockHitResult rayTrace)
    {
        Vec3 location = rayTrace.getLocation();
        return onRightClick(player, stack, Math.round(location.x) < location.x, Math.round(location.z) < location.z);
    }

    public boolean insertItem(Player player, ItemStack stack, BlockHitResult rayTrace)
    {
        Vec3 location = rayTrace.getLocation();
        boolean x = Math.round(location.x) < location.x;
        boolean z = Math.round(location.z) < location.z;
        final int slot = (x ? 1 : 0) + (z ? 2 : 0);
        return insertItem(player, stack, slot);
    }

    /**
     * @return true if an action was taken (passed back through onItemRightClick)
     */
    public boolean onRightClick(Player player, ItemStack stack, boolean x, boolean z)
    {
        final int slot = (x ? 1 : 0) + (z ? 2 : 0);
        if (player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty() || player.isShiftKeyDown())
        {
            ItemStack current;
            if (isHoldingLargeItem)
            {
                current = inventory.getStackInSlot(SLOT_LARGE_ITEM);
            }
            else
            {
                current = inventory.getStackInSlot(slot);
            }

            // Try and grab the item
            if (!current.isEmpty())
            {
                player.addItem(current.split(1));
                inventory.setStackInSlot(slot, ItemStack.EMPTY);

                // This is set to false no matter what happens earlier
                isHoldingLargeItem = false;

                updateBlock();
                return true;
            }
        }
        else if (!stack.isEmpty())
        {
            return insertItem(player, stack, slot);
        }
        return false;
    }

    public boolean insertItem(Player player, ItemStack stack, int slot)
    {
        // Try and insert an item
        // Check the size of item to determine if insertion is possible, or if it requires the large slot
        Size size = ItemSizeManager.get(stack).getSize(stack);
        if (size.isSmallerThan(Size.VERY_LARGE) && !isHoldingLargeItem)
        {
            // Normal and smaller can be placed normally
            if (inventory.getStackInSlot(slot).isEmpty())
            {
                ItemStack input;
                if (player.isCreative())
                {
                    input = stack.copy();
                    input.setCount(1);
                }
                else
                {
                    input = stack.split(1);
                }
                inventory.setStackInSlot(slot, input);
                updateBlock();
                return true;
            }
        }
        else if (!size.isSmallerThan(Size.VERY_LARGE)) // Very Large or Huge
        {
            // Large items are placed in the single center slot
            if (isEmpty())
            {
                ItemStack input;
                if (player.isCreative())
                {
                    input = stack.copy();
                    input.setCount(1);
                }
                else
                {
                    input = stack.split(1);
                }
                inventory.setStackInSlot(SLOT_LARGE_ITEM, input);
                isHoldingLargeItem = true;
                updateBlock();
                return true;
            }
        }
        return false;
    }

    public boolean holdingLargeItem()
    {
        return isHoldingLargeItem;
    }

    @Override
    public void load(CompoundTag nbt)
    {
        isHoldingLargeItem = nbt.getBoolean("isHoldingLargeItem");
        super.load(nbt);
    }

    @Override
    @Nonnull
    public CompoundTag save(CompoundTag nbt)
    {
        nbt.putBoolean("isHoldingLargeItem", isHoldingLargeItem);
        return super.save(nbt);
    }

    protected void updateBlock()
    {
        if (isEmpty() && level != null)
        {
            level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
        }
        else
        {
            markForBlockUpdate();
        }
    }

    protected boolean isEmpty()
    {
        if (isHoldingLargeItem && inventory.getStackInSlot(SLOT_LARGE_ITEM).isEmpty())
        {
            return true;
        }
        for (int i = 0; i < 4; i++)
        {
            if (!inventory.getStackInSlot(i).isEmpty())
            {
                return false;
            }
        }
        return true;
    }
}
