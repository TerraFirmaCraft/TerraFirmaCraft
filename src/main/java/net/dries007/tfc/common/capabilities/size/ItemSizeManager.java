/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.size;

import java.util.ArrayList;
import java.util.List;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import net.minecraft.item.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.registries.ForgeRegistries;

import net.dries007.tfc.mixin.item.ItemAccessor;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.collections.IndirectHashCollection;
import net.dries007.tfc.util.data.DataManager;

import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public final class ItemSizeManager extends DataManager<ItemSizeDefinition>
{
    public static final IndirectHashCollection<Item, ItemSizeDefinition> CACHE = new IndirectHashCollection<>(ItemSizeDefinition::getValidItems);
    public static final ItemSizeManager INSTANCE = new ItemSizeManager();

    private static final List<Item> MODIFIABLE_ITEMS = new ArrayList<>();

    @SuppressWarnings("deprecation")
    public static void setup()
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

    public static void reload()
    {
        CACHE.reload(INSTANCE.getValues());

        // Additionally, compute for every item in the game, the modified stack size
        // Do this once, here, for all items, rather than individually in AttachCapabilitiesEvent handlers
        for (Item item : MODIFIABLE_ITEMS)
        {
            final ItemStack stack = new ItemStack(item);
            final IItemSize size = get(stack);
            ((ItemAccessor) item).accessor$setMaxStackSize(size.getWeight(stack).stackSize);
        }
    }

    public static void addTooltipInfo(ItemStack stack, List<ITextComponent> text)
    {
        IItemSize size = ItemSizeManager.get(stack);
        text.add(new StringTextComponent("\u2696 ")
            .append(new TranslationTextComponent(Helpers.getEnumTranslationKey(size.getWeight(stack))))
            .append(" \u21F2 ")
            .append(new TranslationTextComponent(Helpers.getEnumTranslationKey(size.getSize(stack))))
            .withStyle(TextFormatting.GRAY));
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

    private ItemSizeManager()
    {
        super(new GsonBuilder().create(), "item_sizes", "item size", true);
    }

    @Override
    protected ItemSizeDefinition read(ResourceLocation id, JsonObject obj)
    {
        return new ItemSizeDefinition(id, obj);
    }
}
