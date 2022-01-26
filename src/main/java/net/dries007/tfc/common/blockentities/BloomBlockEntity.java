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
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.blocks.BloomBlock;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class BloomBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    private static final Component NAME = new TranslatableComponent(MOD_ID + ".tile_entity.bloom");

    public BloomBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.BLOOM.get(), pos, state, defaultInventory(1), NAME);
    }

    public void setBloom(ItemStack stack)
    {
        inventory.setStackInSlot(0, stack);
    }

    public ItemStack dropBloom(BlockState state)
    {
        assert level != null;
        //there is no guarantee (especially with custom recipes) of how many blooms will be in the bloom block, so adjust accordingly
        int dropCount = state.getValue(BloomBlock.LAYERS) == 1 ? inventory.getStackInSlot(0).getCount() : inventory.getStackInSlot(0).getCount() / state.getValue(BloomBlock.LAYERS);
        Helpers.spawnItem(level, worldPosition, inventory.getStackInSlot(0).split(dropCount));
        return inventory.getStackInSlot(0);
    }
}
