/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.Helpers;

public class ToolRackBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public ToolRackBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.TOOL_RACK.get(), pos, state, defaultInventory(4));
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.isItem(stack, TFCTags.Items.TOOL_RACK_TOOLS);
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return 1;
    }
}
