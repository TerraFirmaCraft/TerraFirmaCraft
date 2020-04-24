/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects;

import net.minecraft.item.IItemTier;
import net.minecraft.item.crafting.Ingredient;

public final class TFCItemTier implements IItemTier
{
    public static final IItemTier IGNEOUS_INTRUSIVE = new TFCItemTier(1, 60, 7, 2.0f, 5);
    public static final IItemTier SEDIMENTARY = new TFCItemTier(1, 70, 7, 2.0f, 5);
    public static final IItemTier IGNEOUS_EXTRUSIVE = new TFCItemTier(1, 50, 6, 2.0f, 5);
    public static final IItemTier METAMORPHIC = new TFCItemTier(1, 55, 6.5f, 2.0f, 5);
    public static final IItemTier COPPER = new TFCItemTier(2, 600, 8, 3.25f, 8);
    public static final IItemTier BRONZE = new TFCItemTier(2, 1300, 11, 5.0f, 13);
    public static final IItemTier BISMUTH_BRONZE = new TFCItemTier(2, 1200, 10, 4.5f, 10);
    public static final IItemTier BLACK_BRONZE = new TFCItemTier(2, 1460, 9, 4.75f, 10);
    public static final IItemTier IRON = new TFCItemTier(2, 2200, 12, 6.75f, 10);
    public static final IItemTier STEEL = new TFCItemTier(2, 3300, 14, 8.5f, 10);
    public static final IItemTier BLACK_STEEL = new TFCItemTier(3, 4200, 16, 10.25f, 12);
    public static final IItemTier BLUE_STEEL = new TFCItemTier(3, 6500, 18, 12.0f, 22);
    public static final IItemTier RED_STEEL = new TFCItemTier(3, 6500, 18, 12.0f, 22);

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
    public int getMaxUses()
    {
        return durability;
    }

    @Override
    public float getEfficiency()
    {
        return efficiency;
    }

    @Override
    public float getAttackDamage()
    {
        return damage;
    }

    @Override
    public int getHarvestLevel()
    {
        return harvestLevel;
    }

    @Override
    public int getEnchantability()
    {
        return enchantability;
    }

    @Override
    public Ingredient getRepairMaterial()
    {
        return Ingredient.EMPTY; // todo: make this use an item or something
    }
}
