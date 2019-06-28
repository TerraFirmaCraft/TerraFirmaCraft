/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;

import net.dries007.tfc.api.capability.damage.CapabilityDamageResistance;
import net.dries007.tfc.api.capability.damage.IDamageResistance;

/**
 * Resistances are positive, weaknesses are negative
 * Calculations are done per <a href="https://www.desmos.com/calculator/689oqycw1t>this spreadsheet</a>
 */
@ParametersAreNonnullByDefault
public enum DamageType
{
    CRUSHING,
    PIERCING,
    SLASHING,
    GENERIC;

    public static float getModifier(DamageSource source, EntityLivingBase entityUnderAttack)
    {
        DamageType type = DamageType.get(source);
        float resistance = 0;
        if (type != DamageType.GENERIC)
        {
            // Apply damage type specific resistances, from the entity under attack and from their armor
            {
                IDamageResistance resist = entityUnderAttack.getCapability(CapabilityDamageResistance.CAPABILITY, null);
                resistance += type.getModifier(resist);
            }
            if (!source.isUnblockable())
            {
                for (ItemStack stack : entityUnderAttack.getArmorInventoryList())
                {
                    IDamageResistance resist = stack.getCapability(CapabilityDamageResistance.CAPABILITY, null);
                    resistance += type.getModifier(resist);
                }
            }
        }
        return (float) Math.pow(Math.E, -0.01 * resistance);
    }

    @Nonnull
    private static DamageType get(DamageSource source)
    {
        // todo: try and guess what damage type was just dealt, using the various parts of the source
        return GENERIC;
    }

    @Nonnull
    private static DamageType getFromItem(ItemStack stack)
    {
        if (OreDictionaryHelper.doesStackMatchOre(stack, "damageTypeCrushing"))
        {
            return CRUSHING;
        }
        else if (OreDictionaryHelper.doesStackMatchOre(stack, "damageTypeSlashing"))
        {
            return SLASHING;
        }
        else if (OreDictionaryHelper.doesStackMatchOre(stack, "damageTypePiercing"))
        {
            return PIERCING;
        }
        return GENERIC;
    }

    private float getModifier(@Nullable IDamageResistance resistSource)
    {
        if (resistSource != null)
        {
            switch (this)
            {
                case CRUSHING:
                    return resistSource.getCrushingModifier();
                case PIERCING:
                    return resistSource.getPiercingModifier();
                case SLASHING:
                    return resistSource.getSlashingModifier();
            }
        }
        return 1f;
    }
}
