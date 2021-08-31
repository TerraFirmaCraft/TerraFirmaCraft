/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.size;

import java.util.ArrayList;
import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.item.*;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.mixin.accessor.ItemAccessor;
import net.dries007.tfc.util.DataManager;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public final class ItemSizeManager
{
    public static final IndirectHashCollection<Item, ItemSizeDefinition> CACHE = new IndirectHashCollection<>(ItemSizeDefinition::getValidItems);
    public static final DataManager<ItemSizeDefinition> MANAGER = new DataManager.Instance<>(ItemSizeDefinition::new, "item_sizes", "item size");

    private static final List<Item> MODIFIABLE_ITEMS = new ArrayList<>();

    @SuppressWarnings("deprecation")
    public static void setupItemStackSizeOverrides()
    {
        // Initialize the list of editable items here, as we can't rely on checking their stack size later as it may have been modified
        MODIFIABLE_ITEMS.clear();
        for (Item item : ForgeRegistries.ITEMS.getValues())
        {
            // Two checks: item is naturally stackable, and does not *appear* to have stack-specific behavior
            final ItemStack stack = new ItemStack(item);
            if (item.getMaxStackSize() > 1 && stack.getMaxStackSize() == item.getMaxStackSize())
            {
                MODIFIABLE_ITEMS.add(item);
            }
        }
    }

    public static void resetItemSizes()
    {
        // Edit item stack sizes for all editable items in the game (that we can find)
        // Do this once, here, for all items, rather than individually in AttachCapabilitiesEvent handlers
        for (Item item : MODIFIABLE_ITEMS)
        {
            final ItemStack stack = new ItemStack(item);
            final IItemSize size = get(stack);
            ((ItemAccessor) item).accessor$setMaxStackSize(size.getWeight(stack).stackSize);
        }
    }

    public static void addTooltipInfo(ItemStack stack, List<Component> text)
    {
        IItemSize size = ItemSizeManager.get(stack);
        text.add(new TextComponent("\u2696 ")
            .append(Helpers.translateEnum(size.getWeight(stack)))
            .append(" \u21F2 ")
            .append(Helpers.translateEnum(size.getSize(stack)))
            .withStyle(ChatFormatting.GRAY));
    }

    /**
     * Gets the instance to be applied to the given stack during {@link net.minecraftforge.event.AttachCapabilitiesEvent}
     * This is NOT a replacement for {@link ItemStack#getCapability(Capability)}
     */
    public static IItemSize get(ItemStack stack)
    {
        // If the item defines itself as an IItemSize, we use that first
        final Item item = stack.getItem();
        if (item instanceof IItemSize)
        {
            return (IItemSize) item;
        }

        // Definitions
        for (ItemSizeDefinition def : CACHE.getAll(stack.getItem()))
        {
            if (def.matches(stack))
            {
                return def;
            }
        }

        // Default rules
        if (item instanceof TieredItem)
        {
            return ItemSize.of(Size.LARGE, Weight.MEDIUM); // Stored only in chests, stack size should be limited to 1 since it is a tool
        }
        else if (item instanceof ArmorItem)
        {
            return ItemSize.of(Size.LARGE, Weight.VERY_HEAVY); // Stored only in chests and stack size = 1
        }
        else if (item instanceof BlockItem)
        {
            return ItemSize.of(Size.SMALL, Weight.LIGHT); // Fits small vessels and stack size = 32
        }
        else
        {
            return ItemSize.of(Size.VERY_SMALL, Weight.VERY_LIGHT); // Stored anywhere and stack size = 64
        }
    }
}
