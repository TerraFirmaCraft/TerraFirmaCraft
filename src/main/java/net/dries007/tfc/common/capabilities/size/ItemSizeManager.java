/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.size;

import java.util.ArrayList;
import java.util.List;
import com.mojang.logging.LogUtils;
import net.minecraft.ChatFormatting;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.HorseArmorItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;
import org.slf4j.Logger;

import net.dries007.tfc.mixin.accessor.ItemAccessor;
import net.dries007.tfc.network.DataManagerSyncPacket;
import net.dries007.tfc.util.DataManager;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;

public final class ItemSizeManager
{
    public static final DataManager<ItemSizeDefinition> MANAGER = new DataManager<>(Helpers.identifier("item_sizes"), "item size", ItemSizeDefinition::new, ItemSizeDefinition::new, ItemSizeDefinition::encode, Packet::new);
    public static final IndirectHashCollection<Item, ItemSizeDefinition> CACHE = IndirectHashCollection.create(ItemSizeDefinition::getValidItems, MANAGER::getValues);

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final List<Item> MODIFIABLE_ITEMS = new ArrayList<>();

    public static final ItemSize TOOL_SIZE = new ItemSize(Size.LARGE, Weight.MEDIUM); // Stored only in chests, stack size should be limited to 1 since it is a tool
    public static final ItemSize ARMOR_SIZE = new ItemSize(Size.LARGE, Weight.VERY_HEAVY); // Stored only in chests and stack size = 1
    public static final ItemSize BLOCK_SIZE = new ItemSize(Size.SMALL, Weight.LIGHT); // Fits small vessels and stack size = 32
    public static final ItemSize DEFAULT_SIZE = new ItemSize(Size.VERY_SMALL, Weight.VERY_LIGHT); // Stored anywhere and stack size = 64

    @SuppressWarnings("deprecation")
    public static void setupItemStackSizeOverrides()
    {
        // Initialize the list of editable items here, as we can't rely on checking their stack size later as it may have been modified
        for (Item item : BuiltInRegistries.ITEM)
        {
            // Two checks: item is naturally stackable, and does not *appear* to have stack-specific behavior
            final ItemStack stack = new ItemStack(item);
            if (item.getMaxStackSize() > 1 && stack.getMaxStackSize() == item.getMaxStackSize())
            {
                MODIFIABLE_ITEMS.add(item);
            }
        }
    }

    /**
     * After a resource reload, updates all modifiable items (best guess) to have item sizes that reflect the {@link IItemSize}
     */
    public static void applyItemStackSizeOverrides()
    {
        if (MODIFIABLE_ITEMS.isEmpty())
        {
            LOGGER.info("Performing setup for item stack size editing.");
            setupItemStackSizeOverrides();
        }

        LOGGER.info("Editing item stack sizes: found {} editable of {} total.", MODIFIABLE_ITEMS.size(), BuiltInRegistries.ITEM.size());
        for (Item item : MODIFIABLE_ITEMS)
        {
            final ItemStack stack = new ItemStack(item);
            final IItemSize size = get(stack);
            ((ItemAccessor) item).accessor$setMaxStackSize(size.getDefaultStackSize(stack));
        }
    }

    public static void addTooltipInfo(ItemStack stack, List<Component> text)
    {
        final IItemSize size = get(stack);
        text.add(Component.literal("\u2696 ")
            .append(Helpers.translateEnum(size.getWeight(stack)))
            .append(" \u21F2 ")
            .append(Helpers.translateEnum(size.getSize(stack)))
            .withStyle(ChatFormatting.GRAY));
    }

    /**
     * @return the {@link IItemSize} for a given {@code stack}.
     */
    public static IItemSize get(ItemStack stack)
    {
        // If the item (or, a block) defines itself as an IItemSize, we use that first
        final Item item = stack.getItem();
        if (item instanceof IItemSize size)
        {
            return size;
        }
        if (item instanceof BlockItem block && block.getBlock() instanceof IItemSize size)
        {
            return size;
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
        if (item instanceof TieredItem || item instanceof BucketItem)
        {
            return TOOL_SIZE;
        }
        else if (item instanceof ArmorItem || item instanceof HorseArmorItem)
        {
            return ARMOR_SIZE;
        }
        else if (item instanceof BlockItem)
        {
            return BLOCK_SIZE;
        }
        else
        {
            return DEFAULT_SIZE;
        }
    }

    public static class Packet extends DataManagerSyncPacket<ItemSizeDefinition> {}
}
