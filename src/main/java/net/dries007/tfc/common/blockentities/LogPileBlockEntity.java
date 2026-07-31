/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;

import static net.dries007.tfc.common.blocks.devices.LogPileBlock.*;

public class LogPileBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public static final int SLOTS = 16;

    private static final int DOUBLE_CLICK_TICKS = 10;
    private static final long NO_CLICK = Long.MIN_VALUE / 2;

    private boolean needsLogDispersion = true;
    private long lastClickTick;
    private boolean isLastClickPlacement;

    public LogPileBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.LOG_PILE.get(), pos, state, defaultInventory(SLOTS));
        lastClickTick = Calendars.get().getTicks();
        isLastClickPlacement = true;
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

    public boolean checkDoubleClick(long gameTime)
    {
        final boolean doubleClick = gameTime - lastClickTick <= DOUBLE_CLICK_TICKS;
        if (doubleClick)
        {
            lastClickTick = NO_CLICK;
        }
        return doubleClick;
    }

    public void setLastClickTick(long tick)
    {
        this.lastClickTick = tick;
        setChanged();
    }

    public long getLastClickTick()
    {
        return this.lastClickTick;
    }

    public boolean isLastClickPlacement()
    {
        return isLastClickPlacement;
    }

    public void setLastClickPlacement(boolean lastInteractionPlacement)
    {
        isLastClickPlacement = lastInteractionPlacement;
        setChanged();
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
    public void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        tag.putBoolean("placement", isLastClickPlacement);
        tag.putLong("tick", lastClickTick);
        super.saveAdditional(tag, provider);
    }

    @Override
    public void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        isLastClickPlacement = tag.getBoolean("placement");
        lastClickTick = tag.getLong("tick");
        super.loadAdditional(tag, provider);
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
