/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import java.util.List;
import java.util.Map;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.component.DataComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.ItemLore;

import net.dries007.tfc.common.blocks.rock.RockCategory;
import net.dries007.tfc.common.blocks.rock.RockDisplayCategory;
import net.dries007.tfc.util.Helpers;

import static net.minecraft.ChatFormatting.*;

/**
 * {@link ItemLore} tooltips shared with multiple items, across multiple meanings. They are assigned default values as-per the item,
 * but obviously can be modified via either (1) an addon which wishes to, or (2) individual item stacks can be overriden via a custom
 * component. I don't think there's a real interest in giving pack makers control over arbitrary item tooltips in a system like this,
 * they already have the tools to do that if they really wish.
 */
public final class Lore
{
    public static final DataComponentType<ItemLore> TYPE = DataComponents.LORE;

    public static final ItemLore SILICA = of(Component.translatable("tfc.tooltip.glass.silica").withStyle(AQUA, ITALIC));
    public static final ItemLore HEMATITIC = of(Component.translatable("tfc.tooltip.glass.hematitic").withStyle(RED, ITALIC));
    public static final ItemLore OLIVINE = of(Component.translatable("tfc.tooltip.glass.olivine").withStyle(GREEN, ITALIC));
    public static final ItemLore VOLCANIC = of(Component.translatable("tfc.tooltip.glass.volcanic").withStyle(DARK_PURPLE, ITALIC));

    public static final ItemLore UNSEALED = of(Component.translatable("tfc.tooltip.unsealed").withStyle(GRAY, ITALIC));
    public static final ItemLore SEALED = of(Component.translatable("tfc.tooltip.sealed").withStyle(GRAY, ITALIC));

    public static final Map<RockDisplayCategory, ItemLore> ROCK_DISPLAY_CATEGORIES = Helpers.mapOf(RockDisplayCategory.class, type -> of(Helpers.translateEnum(type).withStyle(GRAY, ITALIC)));
    public static final Map<RockCategory, ItemLore> ROCK_CATEGORIES = Helpers.mapOf(RockCategory.class, type -> of(Helpers.translateEnum(type).withStyle(GRAY, ITALIC)));

    private static final Style REMOVE_LORE_STYLE = Style.EMPTY.withColor(GRAY).withItalic(false);

    /**
     * Appends an additional line of text to an existing stack's lore, or creates new lore if it does not exist. This will also override the default
     * style of the component with one that effectively "cancels" the item lore auto-applied style.
     */
    public static void append(ItemStack stack, MutableComponent text)
    {
        stack.update(TYPE, ItemLore.EMPTY, old -> old.withLineAdded(text.withStyle(REMOVE_LORE_STYLE)));
    }

    private static ItemLore of(Component lore)
    {
        return new ItemLore(List.of(lore));
    }
}
