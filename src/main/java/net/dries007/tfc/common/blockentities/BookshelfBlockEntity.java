/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import net.minecraft.core.BlockPos;
import net.minecraft.core.NonNullList;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.ContainerHelper;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.ChiseledBookShelfBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.items.ItemStackHandler;

import net.dries007.tfc.util.Helpers;


public class BookshelfBlockEntity extends ChiseledBookShelfBlockEntity
{
    public BookshelfBlockEntity(BlockPos pos, BlockState state)
    {
        super(pos, state);
    }

    @Override
    public void load(CompoundTag tag)
    {
        if (tag.contains("inventory", Tag.TAG_COMPOUND))
        {
            var legacyInventory = new ItemStackHandler(6);
            legacyInventory.deserializeNBT(tag.getCompound("inventory"));

            final NonNullList<ItemStack> list = NonNullList.create();
            for (ItemStack stack : Helpers.iterate(legacyInventory))
            {
                list.add(stack);
            }
            tag.remove("inventory");
            ContainerHelper.saveAllItems(tag, list, true);
            load(tag);
            return;
        }
        super.load(tag);

    }
}
