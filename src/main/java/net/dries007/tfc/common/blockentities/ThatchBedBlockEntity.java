/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class ThatchBedBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    private static final Component NAME = new TranslatableComponent(MOD_ID + ".tile_entity.thatch_bed");

    public ThatchBedBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.THATCH_BED.get(), pos, state, defaultInventory(3), NAME);
    }

    public void setBed(BlockState head, BlockState foot, ItemStack top)
    {
        assert level != null;
        inventory.setStackInSlot(0, new ItemStack(head.getBlock()));
        inventory.setStackInSlot(1, new ItemStack(foot.getBlock()));
        inventory.setStackInSlot(2, top);
    }


}
