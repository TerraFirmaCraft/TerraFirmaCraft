/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class BowlBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public static final int MAX_POWDER = 16;
    public static final Component NAME = Component.translatable(MOD_ID + ".block_entity.bowl");

    public BowlBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.BOWL.get(), pos, state);
    }

    public BowlBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, defaultInventory(1), NAME);
        if (TFCConfig.SERVER.powderBowlEnableAutomation.get())
        {
            sidedInventory
                .on(new PartialItemHandler(inventory).insertAll(), Direction.UP)
                .on(new PartialItemHandler(inventory).extractAll(), d -> d != Direction.UP);
        }
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        markForSync(); // Override to sync inventory on an update, as it is rendered in-world
    }

    @Override
    public int getSlotStackLimit(int slot)
    {
        return MAX_POWDER;
    }

    @Override
    public boolean isItemValid(int slot, ItemStack stack)
    {
        return Helpers.isItem(stack, TFCTags.Items.POWDERS);
    }
}
