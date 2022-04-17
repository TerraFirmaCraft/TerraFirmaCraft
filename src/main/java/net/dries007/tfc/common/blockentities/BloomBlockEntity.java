/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;

import net.dries007.tfc.common.blocks.BloomBlock;
import net.dries007.tfc.common.blocks.TFCBlocks;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class BloomBlockEntity extends InventoryBlockEntity<ItemStackHandler>
{
    private static final Component NAME = new TranslatableComponent(MOD_ID + ".block_entity.bloom");

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
        BlockPos.MutableBlockPos mutable = new BlockPos.MutableBlockPos();
        BlockPos dropPos = worldPosition;
        for (Direction d : Direction.Plane.HORIZONTAL)
        {
            mutable.set(worldPosition.getX(), worldPosition.getY(), worldPosition.getZ());
            mutable.move(d);
            if (level.getBlockState(mutable).is(TFCBlocks.BLOOMERY.get()))
            {
                dropPos = mutable.immutable();
            }
        }
        //drop bloom at bloomery location, so it's easier to pick up
        Helpers.spawnItem(level, dropPos, inventory.getStackInSlot(0).split(dropCount));
        return inventory.getStackInSlot(0);
    }
}
