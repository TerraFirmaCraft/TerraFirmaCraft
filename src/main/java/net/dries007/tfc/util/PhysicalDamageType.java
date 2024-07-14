/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.List;
import java.util.Locale;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.util.data.EntityDamageResistance;
import net.dries007.tfc.util.data.ItemDamageResistance;

/**
 * Provides a damage type specific resistance calculation.
 * Damage type specific modifications and resistances use an exponential, multiplicative modifier system
 * See <a href="https://www.desmos.com/calculator/689oqycw1t">this spreadsheet</a>.
 */
public enum PhysicalDamageType implements StringRepresentable
{
    CRUSHING,
    SLASHING,
    PIERCING;

    public static final TagKey<DamageType> BYPASSES_DAMAGE_RESISTANCES = TagKey.create(Registries.DAMAGE_TYPE, Helpers.identifier("bypasses_damage_resistances"));
    public static final TagKey<DamageType> IS_PIERCING = TagKey.create(Registries.DAMAGE_TYPE, Helpers.identifier("is_piercing"));
    public static final TagKey<DamageType> IS_CRUSHING = TagKey.create(Registries.DAMAGE_TYPE, Helpers.identifier("is_crushing"));
    public static final TagKey<DamageType> IS_SLASHING = TagKey.create(Registries.DAMAGE_TYPE, Helpers.identifier("is_slashing"));

    public static void addTooltipInfo(ItemStack stack, List<Component> tooltips)
    {
        // Damage type
        final PhysicalDamageType damageType = getTypeForItem(stack);
        if (damageType != null)
        {
            tooltips.add(Component.translatable("tfc.tooltip.deals_damage." + damageType.getSerializedName()));
        }

        // Damage resistance
        final PhysicalDamageType.Multiplier resistanceType = getResistanceForItem(stack);
        if (resistanceType != null)
        {
            tooltips.add(Component.translatable("tfc.tooltip.resists_damage", calculatePercentageForDisplay(resistanceType.slashing()), calculatePercentageForDisplay(resistanceType.piercing()), calculatePercentageForDisplay(resistanceType.crushing())));
        }
    }

    public static float calculateMultiplier(DamageSource source, Entity entityUnderAttack)
    {
        final PhysicalDamageType type = getTypeForSource(source);
        float resistance = 0;

        if (type != null)
        {
            // Natural resistances only apply to specific damage types (because they can be infinite, which would make punching deal zero damage)
            final EntityDamageResistance entityResistance = EntityDamageResistance.get(entityUnderAttack);
            if (entityResistance != null)
            {
                resistance += entityResistance.damages().value(type);
            }
        }

        if (entityUnderAttack instanceof LivingEntity livingEntity)
        {
            for (ItemStack stack : livingEntity.getArmorSlots())
            {
                final PhysicalDamageType.Multiplier armorMultiplier = getResistanceForItem(stack);
                if (armorMultiplier != null)
                {
                    resistance += armorMultiplier.value(type);
                }
            }
        }
        return (float) Math.pow(Math.E, -0.01 * resistance);
    }

    @Nullable
    public static PhysicalDamageType getTypeForSource(DamageSource source)
    {
        if (source.is(BYPASSES_DAMAGE_RESISTANCES))
        {
            return null;
        }
        if (source.is(IS_PIERCING))
        {
            return PIERCING;
        }
        if (source.is(IS_CRUSHING))
        {
            return CRUSHING;
        }
        if (source.is(IS_SLASHING))
        {
            return CRUSHING;
        }

        // Next, try and check for an entity doing the damaging
        final Entity entity = source.getEntity();
        if (entity != null)
        {
            // Check for the attacking weapon
            if (entity instanceof LivingEntity livingEntity)
            {
                final ItemStack heldItem = livingEntity.getMainHandItem();
                if (!heldItem.isEmpty())
                {
                    // Find a unique damage type for the weapon, if it exists
                    final PhysicalDamageType weaponDamageType = getTypeForItem(heldItem);
                    if (weaponDamageType != null)
                    {
                        return weaponDamageType;
                    }
                }
            }

            if (Helpers.isEntity(entity, TFCTags.Entities.DEALS_PIERCING_DAMAGE))
            {
                return PIERCING;
            }
            if (Helpers.isEntity(entity, TFCTags.Entities.DEALS_SLASHING_DAMAGE))
            {
                return SLASHING;
            }
            if (Helpers.isEntity(entity, TFCTags.Entities.DEALS_CRUSHING_DAMAGE))
            {
                return CRUSHING;
            }
        }
        return null;
    }

    @Nullable
    public static PhysicalDamageType getTypeForItem(ItemStack stack)
    {
        if (Helpers.isItem(stack, TFCTags.Items.DEALS_PIERCING_DAMAGE))
        {
            return PIERCING;
        }
        if (Helpers.isItem(stack, TFCTags.Items.DEALS_SLASHING_DAMAGE))
        {
            return SLASHING;
        }
        if (Helpers.isItem(stack, TFCTags.Items.DEALS_CRUSHING_DAMAGE))
        {
            return CRUSHING;
        }
        return null;
    }

    @Nullable
    public static PhysicalDamageType.Multiplier getResistanceForItem(ItemStack stack)
    {
        final ItemDamageResistance itemResistance = ItemDamageResistance.get(stack);
        if (itemResistance != null)
        {
            return itemResistance.damages();
        }
        // todo 1.21, armor material cannot implement this interface, we need a separate mapping here, maybe via registry?
        if (stack.getItem() instanceof ArmorItem armor && armor.getMaterial() instanceof PhysicalDamageType.Multiplier armorMultiplier)
        {
            return armorMultiplier;
        }
        return null;
    }

    private static Component calculatePercentageForDisplay(float resistance)
    {
        final float multiplier = (1 - (float) Math.pow(Math.E, -0.01 * resistance));
        if (multiplier >= 0.999999)
        {
            return Component.translatable("tfc.tooltip.immune_to_damage");
        }
        return Component.literal(String.format("%.0f%%", multiplier * 100));
    }

    private final String serializedName;

    PhysicalDamageType()
    {
        this.serializedName = name().toLowerCase(Locale.ROOT);
    }

    @Override
    public String getSerializedName()
    {
        return serializedName;
    }

    /**
     * A set of defensive multipliers for damage type specific resistances.
     * This is applied when on the {@link net.minecraft.world.item.ArmorMaterial} used by a {@link ArmorItem}, and when defined via a {@link EntityDamageResistance}.
     */
    public interface Multiplier
    {
        float crushing();
        float piercing();
        float slashing();

        default float value(@Nullable PhysicalDamageType type)
        {
            if (type == null)
            {
                // No damage type = use the highest of all resistances.
                return Math.max(crushing(), Math.max(piercing(), slashing()));
            }
            return switch (type)
                {
                    case CRUSHING -> crushing();
                    case SLASHING -> slashing();
                    case PIERCING -> piercing();
                };
        }
    }
}
