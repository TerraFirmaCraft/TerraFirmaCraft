/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.common.container.LogPileContainer;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.common.blocks.devices.LogPileBlock.*;

public class LogPileBlockEntity extends InventoryBlockEntity<ItemStackHandler> implements MenuProvider
{
    public static final int SLOTS = 16;
    private int playersUsing;

    private boolean needsLogDispersion = true;

    private boolean needsSlotUpdate = false;

    public LogPileBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.LOG_PILE.get(), pos, state, defaultInventory(SLOTS));
        this.playersUsing = 0;
    }

    public static void serverTick(Level level, BlockPos pos, BlockState state, LogPileBlockEntity logPile)
    {
        if (logPile.needsSlotUpdate)
        {
            logPile.cascadeLogSlots();
        }
        if (level.getGameTime() % 8 == 0) // hopper speed
        {
            // if there's room above and this pile is full, try and push the logs up
            if (level.getBlockState(pos.above()).isAir() && logPile.logCount() == SLOTS)
            {
                // TODO this is NOT the ideal way to handle this behavior, very gross and prototype-y
                level.setBlockAndUpdate(pos.above(), TFCBlocks.LOG_PILE.get().defaultBlockState());
                if (level.getBlockEntity(pos.above()) instanceof LogPileBlockEntity pileAbove)
                {
                    Helpers.gatherAndConsumeItems(level, new AABB(0f, 0f, 0f, 1f, 1.1f, 1f).move(pos), pileAbove.inventory, 0, SLOTS - 1);
                    if (pileAbove.logCount() == 0){
                        level.removeBlock(pos.above(), false);
                    }
                }

            }
            if (logPile.logCount() < SLOTS)
            {
                Helpers.gatherAndConsumeItems(level, new AABB(0f, 0f, 0f, 1f, 1.1f, 1f).move(pos), logPile.inventory, 0, SLOTS - 1);
            }
        }

        if (logPile.logCount() < SLOTS)
        {
            logPile.suckLogsFromAbove();
        }

    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        needsSlotUpdate = true;
        if (level != null && !level.isClientSide())
        {
            if (playersUsing == 0 && isEmpty())
            {
                level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
            }
            else if (!isEmpty())
            {
                level.setBlockAndUpdate(worldPosition, this.getBlockState().setValue(COUNT, logCount()));
            }
        }
    }

    public void onOpen(Player player)
    {
        if (!player.isSpectator())
        {
            playersUsing++;
        }
    }

    public void onClose(Player player)
    {
        if (!player.isSpectator())
        {
            playersUsing--;
            if (playersUsing < 0)
            {
                playersUsing = 0;
            }
            setAndUpdateSlots(-1);
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

        needsSlotUpdate = false;
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

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int windowID, Inventory inv, Player player)
    {
        return LogPileContainer.create(this, inv, windowID);
    }


}
