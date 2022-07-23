/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.util;

import java.util.List;
import java.util.Locale;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TranslatableComponent;
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
        final PhysicalDamageType type = getTypeForItem(stack);
        if (type != null)
        {
            tooltips.add(new TranslatableComponent("tfc.tooltip.deals_damage." + type.getSerializedName()));
        }
    }

    public static float calculateMultiplier(DamageSource source, Entity entityUnderAttack)
    {
        final PhysicalDamageType type = getTypeForSource(source);
        if (type == null)
        {
            return 1f;
        }

        float resistance = 0;

        final PhysicalDamageType.Multiplier naturalMultiplier = EntityDamageResistance.get(entityUnderAttack);
        if (naturalMultiplier != null)
        {
            resistance += naturalMultiplier.value(type);
        }

        for (ItemStack stack : entityUnderAttack.getArmorSlots())
        {
            if (stack.getItem() instanceof ArmorItem armor && armor.getMaterial() instanceof PhysicalDamageType.Multiplier armorMultiplier)
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

        default float value(PhysicalDamageType type)
        {
            return switch (type)
                {
                    case CRUSHING -> crushing();
                    case SLASHING -> slashing();
                    case PIERCING -> piercing();
                };
        }
    }
}
