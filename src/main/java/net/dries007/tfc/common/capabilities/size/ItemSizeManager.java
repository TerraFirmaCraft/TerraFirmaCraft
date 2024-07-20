/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.size;

import java.util.List;
import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.AnimalArmorItem;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.BucketItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TieredItem;

import net.dries007.tfc.common.recipes.RecipeHelpers;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.DataManager;

public final class ItemSizeManager
{
    public static final DataManager<ItemSizeDefinition> MANAGER = new DataManager<>(Helpers.identifier("item_size"), ItemSizeDefinition.CODEC, ItemSizeDefinition.STREAM_CODEC);
    public static final IndirectHashCollection<Item, ItemSizeDefinition> CACHE = IndirectHashCollection.create(r -> RecipeHelpers.itemKeys(r.ingredient()), MANAGER::getValues);

    public static final ItemSizeDefinition TOOL_SIZE = new ItemSizeDefinition(Size.LARGE, Weight.MEDIUM); // Stored only in chests, stack size should be limited to 1 since it is a tool
    public static final ItemSizeDefinition ARMOR_SIZE = new ItemSizeDefinition(Size.LARGE, Weight.VERY_HEAVY); // Stored only in chests and stack size = 1
    public static final ItemSizeDefinition BLOCK_SIZE = new ItemSizeDefinition(Size.SMALL, Weight.LIGHT); // Fits small vessels and stack size = 32
    public static final ItemSizeDefinition DEFAULT_SIZE = new ItemSizeDefinition(Size.VERY_SMALL, Weight.VERY_LIGHT); // Stored anywhere and stack size = 64

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

        return getDefinition(stack);
    }

    public static ItemSizeDefinition getDefinition(ItemStack stack)
    {
        // Definitions
        final Item item = stack.getItem();
        for (ItemSizeDefinition def : CACHE.getAll(stack.getItem()))
        {
            if (def.ingredient().test(stack))
            {
                return def;
            }
        }

        // Default rules
        if (item instanceof TieredItem || item instanceof BucketItem)
        {
            return TOOL_SIZE;
        }
        else if (item instanceof ArmorItem || item instanceof AnimalArmorItem)
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
}
