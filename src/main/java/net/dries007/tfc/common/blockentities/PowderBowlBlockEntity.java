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
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.PartialItemHandler;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.*;

public class PowderBowlBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    public static final int MAX_POWDER = 16;
    public static final Component NAME = Component.translatable(MOD_ID + ".block_entity.powder_bowl");

    public PowderBowlBlockEntity(BlockPos pos, BlockState state)
    {
        this(TFCBlockEntities.POWDER_BOWL.get(), pos, state);
    }

    public PowderBowlBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
    {
        super(type, pos, state, defaultInventory(1), NAME);
        if (TFCConfig.SERVER.powderBowlEnableAutomation.get())
        {
            sidedInventory
                .on(new PartialItemHandler(inventory).insertAll(), Direction.UP)
                .on(new PartialItemHandler(inventory).extractAll(), Direction.DOWN);
        }
    }

    @Override
    public void setAndUpdateSlots(int slot)
    {
        super.setAndUpdateSlots(slot);
        markForSync();
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
