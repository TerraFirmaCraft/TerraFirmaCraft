/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;

import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.Metal;

public class IngotPileBlockEntity extends TFCBlockEntity
{
    private final List<ItemStack> stacks;
    private final List<Metal> cachedMetals;

    public IngotPileBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.INGOT_PILE.get(), pos, state);

        stacks = new ArrayList<>();
        cachedMetals = new ArrayList<>();
    }

    public void addIngot(ItemStack stack)
    {
        assert stacks.size() == cachedMetals.size();

        stacks.add(stack);
        cachedMetals.add(null);
        markForSync();
    }

    public List<ItemStack> removeAllIngots()
    {
        assert stacks.size() == cachedMetals.size();

        final List<ItemStack> stacks = new ArrayList<>(this.stacks);
        this.stacks.clear();
        this.cachedMetals.clear();
        markForSync();
        return stacks;
    }

    public ItemStack removeIngot()
    {
        assert stacks.size() == cachedMetals.size();

        final ItemStack remove = stacks.remove(stacks.size() - 1);
        cachedMetals.remove(cachedMetals.size() - 1);
        markForSync();
        return remove;
    }

    /**
     * Returns a cached metal for the given side, if present, otherwise grabs from the cache.
     * The metal is defined by checking what metal the stack would melt into if heated.
     * Any other items turn into {@link Metal#unknown()}.
     */
    public Metal getOrCacheMetal(int index)
    {
        assert stacks.size() == cachedMetals.size();

        if (index >= stacks.size())
        {
            return Metal.unknown();
        }

        final ItemStack stack = stacks.get(index);

        Metal metal = cachedMetals.get(index);
        if (metal == null)
        {
            metal = Metal.getFromIngot(stack);
            if (metal == null)
            {
                metal = Metal.unknown();
            }
            cachedMetals.set(index, metal);
        }
        return metal;
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
        cachedMetals.clear();
        for (int i = 0; i < stacks.size(); i++)
        {
            cachedMetals.add(null);
        }
        super.loadAdditional(tag);
    }
}
