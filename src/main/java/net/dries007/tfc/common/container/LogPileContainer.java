/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.container;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.common.blockentities.CharcoalForgeBlockEntity;
import net.dries007.tfc.common.blockentities.LogPileBlockEntity;
import net.dries007.tfc.common.blocks.devices.CharcoalForgeBlock;
import net.dries007.tfc.common.component.heat.Heat;
import net.dries007.tfc.common.component.heat.HeatCapability;
import net.dries007.tfc.common.component.heat.IHeat;
import net.dries007.tfc.common.container.slot.CallbackSlot;
import net.dries007.tfc.util.Helpers;

public class LogPileContainer extends BlockEntityContainer<LogPileBlockEntity>
{
    public static LogPileContainer create(LogPileBlockEntity logPile, Inventory playerInventory, int windowId)
    {
        return new LogPileContainer(logPile, playerInventory, windowId).init(playerInventory, 20);
    }

    public LogPileContainer(LogPileBlockEntity logPile, Inventory playerInventory, int windowId)
    {
        super(TFCContainerTypes.LOG_PILE.get(), windowId, logPile);
        logPile.onOpen(playerInventory.player);
    }

    @Override
    protected boolean moveStack(ItemStack stack, int slotIndex)
    {
        return switch (typeOf(slotIndex))
        {
            case MAIN_INVENTORY, HOTBAR -> !moveItemStackTo(stack, 0, LogPileBlockEntity.SLOTS, false);
            case CONTAINER -> !moveItemStackTo(stack, containerSlots, slots.size(), false);
        };
    }

    @Override
    public void removed(Player player)
    {
        blockEntity.onClose(player);
        super.removed(player);
    }

    @Override
    protected void addContainerSlots()
    {
        final int xfirst = 53;
        final int yfirst = 16;
        final int spacing = 18;
        int i = 0;
        for (int y = 3; y >= 0; y--)
        {
            for (int x = 0; x < 4; x++)
            {
                addSlot(new CallbackSlot(blockEntity, i, xfirst + x * spacing, yfirst + y * spacing));
                i++;
            }
        }
    }
}
