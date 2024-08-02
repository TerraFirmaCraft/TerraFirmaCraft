/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.container.LogPileContainer;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class LogPileBlockEntity extends InventoryBlockEntity<ItemStackHandler> implements MenuProvider
{
    public static final int SLOTS = 4;

    private int playersUsing;

    public LogPileBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.LOG_PILE.get(), pos, state, defaultInventory(SLOTS));
        this.playersUsing = 0;
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        if (level != null && !level.isClientSide())
        {
            if (playersUsing == 0 && isEmpty())
            {
                level.setBlockAndUpdate(worldPosition, Blocks.AIR.defaultBlockState());
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
            count += stack.getCount();
        }
        return count;
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return SLOTS;
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
