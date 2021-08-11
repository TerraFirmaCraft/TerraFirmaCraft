/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import net.minecraft.world.item.Tier;
import net.minecraft.world.item.crafting.Ingredient;

public final class TFCItemTier implements Tier
{
    // Tier 0
    public static final Tier IGNEOUS_INTRUSIVE = new TFCItemTier(0, 60, 3.0f, 2.0f, 5);
    public static final Tier IGNEOUS_EXTRUSIVE = new TFCItemTier(0, 70, 3.0f, 2.0f, 5);
    public static final Tier SEDIMENTARY = new TFCItemTier(0, 50, 2.5f, 2.0f, 5);
    public static final Tier METAMORPHIC = new TFCItemTier(0, 55, 2.75f, 2.0f, 5);
    // Tier 1
    public static final Tier COPPER = new TFCItemTier(1, 600, 4.5f, 3.25f, 8);
    // Tier 2
    public static final Tier BRONZE = new TFCItemTier(2, 1300, 6.5f, 4.0f, 13);
    public static final Tier BISMUTH_BRONZE = new TFCItemTier(2, 1200, 6.0f, 4.0f, 10);
    public static final Tier BLACK_BRONZE = new TFCItemTier(2, 1460, 5.5f, 4.25f, 10);
    // Tier 3
    public static final Tier WROUGHT_IRON = new TFCItemTier(3, 2200, 8.0f, 4.75f, 12);
    // Tier 4
    public static final Tier STEEL = new TFCItemTier(4, 3300, 9.5f, 5.75f, 12);
    // Tier 5
    public static final Tier BLACK_STEEL = new TFCItemTier(5, 4200, 11.0f, 7.0f, 17);
    // Tier 6
    public static final Tier BLUE_STEEL = new TFCItemTier(6, 6500, 12.0f, 9.0f, 22);
    public static final Tier RED_STEEL = new TFCItemTier(6, 6500, 12.0f, 9.0f, 22);

    private final int harvestLevel;
    private final int durability;
    private final float efficiency;
    private final float damage;
    private final int enchantability;

    private TFCItemTier(int harvestLevel, int durability, float efficiency, float damage, int enchantability)
    {
        this.harvestLevel = harvestLevel;
        this.durability = durability;
        this.efficiency = efficiency;
        this.damage = damage;
        this.enchantability = enchantability;
    }

    @Override
    public int getUses()
    {
        return durability;
    }

    @Override
    public float getSpeed()
    {
        return efficiency;
    }

    @Override
    public float getAttackDamageBonus()
    {
        return damage;
    }

    @Override
    public int getLevel()
    {
        return harvestLevel;
    }

    @Override
    public int getEnchantmentValue()
    {
        return enchantability;
    }

    @Override
    public Ingredient getRepairIngredient()
    {
        // TFC items can't be repaired
        return Ingredient.EMPTY;
    }
}