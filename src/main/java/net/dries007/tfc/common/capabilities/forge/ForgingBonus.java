/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.capabilities.forge;

import java.util.List;
import java.util.Random;

import net.minecraft.ChatFormatting;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.util.Helpers;

public enum ForgingBonus
{
    NONE(Float.POSITIVE_INFINITY, 1.0f, 0f, 1.0f),
    POORLY_FORGED(10.0f, 1.2f, 0.125f, 1.125f),
    WELL_FORGED(5.0f, 1.4f, 0.25f, 1.25f),
    EXPERTLY_FORGED(2.0f, 1.6f, 0.375f, 1.375f),
    PERFECTLY_FORGED(1.5f, 1.8f, 0.5f, 1.5f);

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
            tooltips.add(Helpers.translateEnum(bonus).withStyle(ChatFormatting.GREEN));
        }
    }

    /**
     * Mimics unbreaking behavior for higher forging bonuses.
     *
     * @return {@code true} if the damage was consumed.
     * @see ItemStack#hurt(int, Random, ServerPlayer)
     */
    public static boolean applyLikeUnbreaking(ItemStack stack, Random random)
    {
        if (stack.isDamageableItem())
        {
            final ForgingBonus bonus = get(stack);
            if (bonus != NONE)
            {
                return random.nextFloat() < bonus.durability();
            }
        }
        return false;
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
    private final float efficiency;
    private final float durability;
    private final float damage;

    ForgingBonus(float maxRatio, float efficiency, float durability, float damage)
    {
        this.maxRatio = maxRatio;
        this.efficiency = efficiency;
        this.durability = durability;
        this.damage = damage;
    }

    public float efficiency()
    {
        return efficiency;
    }

    public float durability()
    {
        return durability;
    }

    public float damage()
    {
        return damage;
    }
}
