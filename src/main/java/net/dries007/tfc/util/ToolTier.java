/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.function.Supplier;
import javax.annotation.Nonnull;

import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.level.block.Block;

/**
 * Forge you ass why did you make {@link net.minecraftforge.common.ForgeTier} final
 * All I wanted was a useful implementation of {@link #toString()} for crying out loud.
 */
public record ToolTier(String name, int level, int uses, float speed, float attackDamageBonus, int enchantmentValue, TagKey<Block> tag, Supplier<Ingredient> repairIngredient) implements Tier
{
    @Override
    public int getUses()
    {
        return uses;
    }

    @Override
    public float getSpeed()
    {
        return speed;
    }

    @Override
    public float getAttackDamageBonus()
    {
        return attackDamageBonus;
    }

    @Override
    @Deprecated
    public int getLevel()
    {
        return level;
    }

    @Override
    public int getEnchantmentValue()
    {
        return enchantmentValue;
    }

    @Nonnull
    public TagKey<Block> getTag()
    {
        return tag;
    }

    @Nonnull
    @Override
    public Ingredient getRepairIngredient()
    {
        return repairIngredient.get();
    }

    @Override
    public String toString()
    {
        return name;
    }
}
