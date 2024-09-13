/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.blocks.devices.LogPileBlock.*;

public class LogPileBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public static final int SLOTS = 16;

    private boolean needsLogDispersion = true;

    public LogPileBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.LOG_PILE.get(), pos, state, defaultInventory(SLOTS));
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        if (level != null && !level.isClientSide())
        {
            suckLogsFromAbove();
            if (isEmpty())
            {
                level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
            }
            else if (!isEmpty())
            {
                level.setBlockAndUpdate(worldPosition, this.getBlockState().setValue(COUNT, logCount()));
            }
        }
    }

    public boolean isEmpty()
    {
        for (ItemStack stack : Helpers.iterate(inventory))
        {
            if (!stack.isEmpty())
            {
                return false;
            }
        }
        return true;
    }

    public int logCount()
    {
        int count = 0;
        for (ItemStack stack : Helpers.iterate(inventory))
        {
            if (!stack.isEmpty())
            {
                count++;
            }

        }
        return count;
    }

    private void cascadeLogSlots()
    {
        // This will cascade all logs down to the lowest available slot
        int lowestAvailSlot = 0;
        for (int i = 0; i < SLOTS; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            if (!stack.isEmpty())
            {
                // Move to lowest available slot
                if (i > lowestAvailSlot)
                {
                    inventory.setStackInSlot(lowestAvailSlot, stack.copy());
                    inventory.setStackInSlot(i, ItemStack.EMPTY);
                }
                lowestAvailSlot++;
            }
        }
    }

    private void suckLogsFromAbove()
    {
        if (level != null && !level.isClientSide())
        {
            if (level.getBlockEntity(this.getBlockPos().above()) instanceof LogPileBlockEntity logPileAbove && !logPileAbove.isEmpty())
            {
                for (int i = 0; i < SLOTS; i++)
                {
                    ItemStack stack = logPileAbove.inventory.getStackInSlot(i);
                    if (!stack.isEmpty())
                    {
                        // Move to an available empty slot
                        for (int j = 0; j < SLOTS; j++)
                        {
                            ItemStack moveToStack = inventory.getStackInSlot(j);
                            if (moveToStack.isEmpty())
                            {
                                inventory.setStackInSlot(j, stack.split(1));
                            }
                        }
                    }
                }
                logPileAbove.setAndUpdateSlots(-1);
            }

        }
    }

    private void disperseLogsToNewSlots()
    {
        for (int i = 0; i < SLOTS; i++)
        {
            ItemStack stack = inventory.getStackInSlot(i);
            while (stack.getCount() > getSlotStackLimit(i))
            {
                // Move to an available empty slot
                for (int j = 0; j < SLOTS; j++)
                {
                    ItemStack moveToStack = inventory.getStackInSlot(j);
                    if (moveToStack.isEmpty())
                    {
                        inventory.setStackInSlot(j, stack.split(1));
                    }
                }
            }
        }
    }

    @Override
    protected void onLoadAdditional()
    {
        if (needsLogDispersion)
        {
            disperseLogsToNewSlots();
            needsLogDispersion = false;
        }
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.isItem(stack.getItem(), TFCTags.Items.LOG_PILE_LOGS);
    }


}
