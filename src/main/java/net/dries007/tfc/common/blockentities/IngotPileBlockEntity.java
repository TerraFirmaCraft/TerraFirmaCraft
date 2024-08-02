/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.blockentities;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.util.MetalItem;

public class IngotPileBlockEntity extends TFCBlockEntity
{
    private final List<Entry> entries;

    public IngotPileBlockEntity(BlockPos pos, BlockState state)
    {
        super(TFCBlockEntities.INGOT_PILE.get(), pos, state);

        entries = new ArrayList<>();
    }

    public void addIngot(ItemStack stack)
    {
        entries.add(new Entry(stack));
        markForSync();
    }

    public void removeAllIngots(Consumer<ItemStack> ingotConsumer)
    {
        for (Entry entry : this.entries)
        {
            ingotConsumer.accept(entry.stack);
        }
        this.entries.clear();
        markForSync();
    }

    public ItemStack removeIngot()
    {
        if (!entries.isEmpty())
        {
            final Entry entry = entries.remove(entries.size() - 1);
            markForSync();
            return entry.stack;
        }
        return ItemStack.EMPTY;
    }

    /**
     * Returns a cached metal for the given side, if present, otherwise grabs from the cache.
     * The metal is defined by checking what metal the stack would melt into if heated.
     * Any other items turn into {@link MetalItem#unknown()}.
     */
    public MetalItem getOrCacheMetal(int index)
    {
        if (index >= entries.size())
        {
            return MetalItem.unknown();
        }

        final Entry entry;
        try
        {
            entry = entries.get(index);
        }
        catch (IndexOutOfBoundsException e)
        {
            // This is terrible, but it's a threadsafety issue. `entries` might be updated between the bounds check above, and this query
            return MetalItem.unknown();
        }

        if (entry.metal == null)
        {
            entry.metal = MetalItem.getOrUnknown(entry.stack);
        }
        return entry.metal;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        final ListTag stacks = new ListTag();
        for (final Entry entry : entries)
        {
            stacks.add(entry.stack.save(provider));
        }
        tag.put("stacks", stacks);
        super.saveAdditional(tag, provider);
    }

    @Override
    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider provider)
    {
        entries.clear();
        final ListTag list = tag.getList("stacks", Tag.TAG_COMPOUND);
        for (int i = 0; i < list.size(); i++)
        {
            entries.add(new Entry(ItemStack.parseOptional(provider, list.getCompound(i))));
        }
        super.loadAdditional(tag, provider);
    }

    public void fillTooltip(Consumer<Component> tooltip)
    {
        class Counter
        {
            final ItemStack stack;
            int count = 0;

            Counter(ItemStack stack) { this.stack = stack; }
        }

        final Map<MetalItem, Counter> counts = new LinkedHashMap<>(); // Deterministic iteration order
        for (Entry entry : entries)
        {
            if (entry.metal != null)
            {
                counts.compute(entry.metal, (key, old) -> {
                    if (old == null) old = new Counter(entry.stack);
                    old.count++;
                    return old;
                });
            }
        }
        for (Counter value : counts.values())
        {
            tooltip.accept(Component.literal(value.count + "x ").append(value.stack.getHoverName()));
        }
    }

    public ItemStack getPickedItemStack()
    {
        return entries.isEmpty() ? ItemStack.EMPTY : entries.get(0).stack.copy();
    }

    static class Entry
    {
        final ItemStack stack;
        @Nullable MetalItem metal;

        Entry(ItemStack stack)
        {
            this.stack = stack;
        }
    }
}
