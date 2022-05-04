/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.forge;

import java.util.List;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.util.Helpers;

public enum ForgingBonus
{
    NONE(Float.POSITIVE_INFINITY),
    POORLY_FORGED(5.0f),
    WELL_FORGED(2.5f),
    EXPERTLY_FORGED(1.5f),
    PERFECTLY_FORGED(1.1f);

    private static final String KEY = "tfc:forging_bonus";
    private static final ForgingBonus[] VALUES = values();

    public static ForgingBonus valueOf(int i)
    {
        return i < 0 ? VALUES[0] : (i >= VALUES.length ? VALUES[VALUES.length - 1] : VALUES[i]);
    }

    public static ForgingBonus byRatio(float ratio)
    {
        for (int i = VALUES.length - 1; i > 0; i--)
        {
            if (VALUES[i].maxRatio > ratio)
            {
                return VALUES[i];
            }
        }
        return NONE;
    }

    public static void addTooltipInfo(ItemStack stack, List<Component> tooltips)
    {
        final ForgingBonus bonus = get(stack);
        if (bonus != NONE)
        {
            tooltips.add(Helpers.translateEnum(bonus));
        }
    }

    /**
     * Get the forging bonus currently attached to an item stack.
     */
    public static ForgingBonus get(ItemStack stack)
    {
        final CompoundTag tag = stack.getTag();
        if (tag != null && tag.contains(KEY, Tag.TAG_INT))
        {
            return valueOf(tag.getInt(KEY));
        }
        return NONE;
    }

    /**
     * Set the forging bonus on an item stack
     */
    public static void set(ItemStack stack, ForgingBonus bonus)
    {
        if (bonus != NONE)
        {
            stack.getOrCreateTag().putInt(KEY, bonus.ordinal());
        }
        else
        {
            final CompoundTag tag = stack.getTag();
            if (tag != null && tag.contains(KEY, Tag.TAG_INT))
            {
                tag.remove(KEY);
            }
        }
    }

    private final float maxRatio;

    ForgingBonus(float maxRatio)
    {
        this.maxRatio = maxRatio;
    }
}
