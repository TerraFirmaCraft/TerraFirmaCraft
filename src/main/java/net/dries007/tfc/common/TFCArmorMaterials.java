/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import java.util.Locale;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.PhysicalDamageType;

public enum TFCArmorMaterials implements ArmorMaterial, PhysicalDamageType.Multiplier
{
    COPPER(160, 200, 215, 150, 1, 3, 4, 1, 9, 0f, 0f, 10, 10, 6.25f),
    BISMUTH_BRONZE( 250, 288, 311, 240, 1, 4, 4, 1, 9, 0f, 0f, 15, 10, 8.25f),
    BLACK_BRONZE(285, 340, 336, 262, 1, 4, 4, 1, 9, 0f, 0f, 10, 15, 8.25f),
    BRONZE(270, 315, 323, 251, 1, 4, 4, 1, 9, 0f, 0f, 12.5f, 12.5f, 8.25f),
    WROUGHT_IRON(429, 495, 528, 370, 1, 4, 5, 2, 12, 0f, 0f, 20, 20, 13.2f),
    STEEL(520, 600, 640, 440, 2, 5, 6, 2, 12, 1f, 0f, 25, 30, 16.5f),
    BLACK_STEEL(650, 750, 800, 550, 2, 5, 6, 2, 17, 2f, 0.05f, 50, 45, 33),
    BLUE_STEEL(860, 960, 1088, 748, 3, 6, 8, 3, 23, 3f, 0.1f, 62.5f, 50, 50),
    RED_STEEL(884, 1020, 1010, 715, 3, 6, 8, 3, 23, 3f, 0.1f, 50, 62.5f, 50);

    private final ResourceLocation serializedName;
    private final int feetDamage;
    private final int legDamage;
    private final int chestDamage;
    private final int headDamage;
    private final int feetReduction;
    private final int legReduction;
    private final int chestReduction;
    private final int headReduction;
    private final int enchantability;
    private final float toughness;
    private final float knockbackResistance;
    private final float crushingModifier;
    private final float piercingModifier;
    private final float slashingModifier;

    TFCArmorMaterials(int feetDamage, int legDamage, int chestDamage, int headDamage, int feetReduction, int legReduction, int chestReduction, int headReduction, int enchantability, float toughness, float knockbackResistance, float crushingModifier, float piercingModifier, float slashingModifier)
    {
        this.serializedName = Helpers.identifier(name().toLowerCase(Locale.ROOT));
        this.feetDamage = feetDamage;
        this.legDamage = legDamage;
        this.chestDamage = chestDamage;
        this.headDamage = headDamage;
        this.feetReduction = feetReduction;
        this.legReduction = legReduction;
        this.chestReduction = chestReduction;
        this.headReduction = headReduction;
        this.enchantability = enchantability;
        this.toughness = toughness;
        this.knockbackResistance = knockbackResistance;

        // Since each slot is applied separately, the input values are values for a full set of armor of this type.
        this.crushingModifier = crushingModifier * 0.25f;
        this.piercingModifier = piercingModifier * 0.25f;
        this.slashingModifier = slashingModifier * 0.25f;
    }

    @Override
    public float crushing()
    {
        return crushingModifier;
    }

    @Override
    public float piercing()
    {
        return piercingModifier;
    }

    @Override
    public float slashing()
    {
        return slashingModifier;
    }

    @Override
    public int getDurabilityForSlot(EquipmentSlot slot)
    {
        return switch (slot)
            {
                case FEET -> feetDamage;
                case LEGS -> legDamage;
                case CHEST -> chestDamage;
                case HEAD -> headDamage;
                default -> 0;
            };
    }

    @Override
    public int getDefenseForSlot(EquipmentSlot slot)
    {
        return switch (slot)
            {
                case FEET -> feetReduction;
                case LEGS -> legReduction;
                case CHEST -> chestReduction;
                case HEAD -> headReduction;
                default -> 0;
            };
    }

    @Override
    public int getEnchantmentValue()
    {
        return enchantability;
    }

    @Override
    public SoundEvent getEquipSound()
    {
        return TFCSounds.ARMOR_EQUIP.get(this).get();
    }

    /**
     * Use {@link #getId()} because it doesn't have weird namespaced side effects.
     */
    @Override
    @Deprecated
    public String getName()
    {
        // Note that in HumanoidArmorLayer, the result of getName() is used directly in order to infer the armor texture location
        // So, this needs to be properly namespaced despite being a string.
        return serializedName.toString();
    }

    public ResourceLocation getId()
    {
        return serializedName;
    }

    @Override
    public float getToughness()
    {
        return toughness;
    }

    @Override
    public float getKnockbackResistance()
    {
        return knockbackResistance;
    }

    @Override
    public Ingredient getRepairIngredient()
    {
        return Ingredient.EMPTY;
    }
}