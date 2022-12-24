/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.Arrays;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

public class SheetPileBlockEntity extends TFCBlockEntity
{
    private final ItemStack[] stacks;
    private final Metal[] cachedMetals;

    public SheetPileBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.SHEET_PILE.get(), pos, state);

        this.stacks = new ItemStack[6];
        this.cachedMetals = new Metal[6];

        Arrays.fill(stacks, ItemStack.EMPTY);
    }

    public void addSheet(int index, ItemStack stack)
    {
        stacks[index] = stack;
        cachedMetals[index] = null;
        markForSync();
    }

    public ItemStack removeSheet(int index)
    {
        final ItemStack stack = stacks[index];
        stacks[index] = ItemStack.EMPTY;
        cachedMetals[index] = null;
        markForSync();
        return stack;
    }

    public ItemStack getSheet(int index)
    {
        return stacks[index].copy();
    }

    /**
     * Returns a cached metal for the given side, if present, otherwise grabs from the cache.
     * The metal is defined by checking what metal the stack would melt into if heated.
     * Any other items turn into {@link Metal#unknown()}.
     */
    public Metal getOrCacheMetal(int index)
    {
        final ItemStack stack = stacks[index];

        Metal metal = cachedMetals[index];
        if (metal == null)
        {
            metal = Metal.getFromSheet(stack);
            if (metal == null)
            {
                metal = Metal.unknown();
            }
            cachedMetals[index] = metal;
        }
        return metal;
    }

    /**
     * Sets the cached metals for a block entity that is not placed in the world
     */
    public void setAllMetalsFromOutsideWorld(Metal metal)
    {
        Arrays.fill(cachedMetals, metal);
    }

    @Override
    protected void saveAdditional(CompoundTag tag)
    {
        tag.put("stacks", Helpers.writeItemStacksToNbt(stacks));
        super.saveAdditional(tag);
    }

    @Override
    protected void loadAdditional(CompoundTag tag)
    {
        Helpers.readItemStacksFromNbt(stacks, tag.getList("stacks", Tag.TAG_COMPOUND));
        Arrays.fill(cachedMetals, null); // Invalidate metal cache
        super.loadAdditional(tag);
    }
}
