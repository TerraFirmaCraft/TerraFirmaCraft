/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.common.component.TFCComponents;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.config.TFCConfig;

public class CrateBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public static final int SLOTS = 36;

    private static final int DOUBLE_CLICK_TICKS = 10;
    private static final long NO_CLICK = Long.MIN_VALUE / 2;

    private long lastClickTick = NO_CLICK;

    public CrateBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.CRATE.get(), pos, state);
    }

    public CrateBlockEntity(BlockEntityType<? extends CrateBlockEntity> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, defaultInventory(SLOTS));
        if (TFCConfig.SERVER.largeVesselEnableAutomation.get())
        {
            sidedInventory.on(new PartialItemHandler(inventory).insertAll(), d -> d != Direction.DOWN);
            sidedInventory.on(new PartialItemHandler(inventory).extractAll(), Direction.DOWN);
        }
    }

    public void recordInsertClick(long gameTime)
    {
        lastClickTick = gameTime;
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

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        if (!TFCChestBlockEntity.isValid(stack))
            return false;
        if (stack.has(TFCComponents.CONTENTS) || stack.has(TFCComponents.VESSEL) || stack.has(DataComponents.BUNDLE_CONTENTS) || stack.has(DataComponents.CONTAINER))
            return false;
        for (int i = 0; i < SLOTS; i++)
        {
            final ItemStack contained = inventory.getStackInSlot(i);
            if (!contained.isEmpty())
            {
                return ItemStack.isSameItemSameComponents(contained, stack)
                    || FoodCapability.areStacksStackableExceptCreationDate(contained, stack);
            }
        }
        return true;
    }
}
