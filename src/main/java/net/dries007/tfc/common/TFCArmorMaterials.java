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

import net.dries007.tfc.client.TFCSounds;
import net.dries007.tfc.util.Helpers;

public enum TFCArmorMaterials implements TFCArmorMaterial
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

    private final ResourceLocation location;
    private final String serializedName;
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
        this.location = Helpers.identifier(name().toLowerCase(Locale.ROOT));
        this.serializedName = location.toString();
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
        this.crushingModifier = crushingModifier;
        this.piercingModifier = piercingModifier;
        this.slashingModifier = slashingModifier;
    }

    /**
     * Returns the crushing modifier this armor has
     *
     * @return float value with the modifier. To check how damage calculation is done
     */
    @Override
    public float getCrushingModifier()
    {
        return crushingModifier;
    }

    /**
     * Returns the crushing modifier this armor has
     *
     * @return float value with the modifier. To check how damage calculation is done
     */
    @Override
    public float getPiercingModifier()
    {
        return piercingModifier;
    }

    /**
     * Returns the crushing modifier this armor has
     *
     * @return float value with the modifier. To check how damage calculation is done
     */
    @Override
    public float getSlashingModifier()
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

    @Override
    public String getName()
    {
        return serializedName;
    }

    @Override
    public ResourceLocation getLocation()
    {
        return location;
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
}