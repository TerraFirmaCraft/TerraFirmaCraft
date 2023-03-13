/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.List;
import java.util.Locale;

import net.minecraft.network.chat.Component;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;

import net.dries007.tfc.common.TFCTags;
import org.jetbrains.annotations.Nullable;

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

    // Specific damage source names
    private static final String THORNS = "thorns";
    private static final String ARROW = "arrow";
    private static final String TRIDENT = "trident";

    public static void addTooltipInfo(ItemStack stack, List<Component> tooltips)
    {
        // Damage type
        final PhysicalDamageType damageType = getTypeForItem(stack);
        if (damageType != null)
        {
            tooltips.add(Helpers.translatable("tfc.tooltip.deals_damage." + damageType.getSerializedName()));
        }

        // Damage resistance
        final PhysicalDamageType.Multiplier resistanceType = getResistanceForItem(stack);
        if (resistanceType != null)
        {
            tooltips.add(Helpers.translatable("tfc.tooltip.resists_damage",
                calculatePercentageForDisplay(resistanceType.slashing()),
                calculatePercentageForDisplay(resistanceType.piercing()),
                calculatePercentageForDisplay(resistanceType.crushing())));
        }
    }

    public static float calculateMultiplier(DamageSource source, Entity entityUnderAttack)
    {
        final PhysicalDamageType type = getTypeForSource(source);
        float resistance = 0;

        if (type != null)
        {
            // Natural resistances only apply to specific damage types (because they can be infinite, which would make punching deal zero damage)
            final PhysicalDamageType.Multiplier naturalMultiplier = EntityDamageResistance.get(entityUnderAttack);
            if (naturalMultiplier != null)
            {
                resistance += naturalMultiplier.value(type);
            }
        }

        for (ItemStack stack : entityUnderAttack.getArmorSlots())
        {
            final PhysicalDamageType.Multiplier armorMultiplier = getResistanceForItem(stack);
            if (armorMultiplier != null)
            {
                resistance += armorMultiplier.value(type);
            }
        }
        return (float) Math.pow(Math.E, -0.01 * resistance);
    }

    @Nullable
    public static PhysicalDamageType getTypeForSource(DamageSource source)
    {
        if (source.isBypassArmor() || source.isBypassInvul())
        {
            return null;
        }

        if (source == DamageSource.CACTUS || source == DamageSource.FALLING_STALACTITE || source.getMsgId().equals(THORNS) || source.getMsgId().equals(TRIDENT) || source.getMsgId().equals(ARROW))
        {
            return PIERCING;
        }
        if (source == DamageSource.ANVIL || source == DamageSource.FALLING_BLOCK)
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
        var res = ItemDamageResistance.get(stack);
        if (res != null)
        {
            return res;
        }
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
            return Helpers.translatable("tfc.tooltip.immune_to_damage");
        }
        return Helpers.literal(String.format("%.0f%%", multiplier * 100));
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
