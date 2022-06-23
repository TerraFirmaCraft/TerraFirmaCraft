/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.crafting.Ingredient;

/**
 * @see TFCArmorMaterials
 */
public interface TFCArmorMaterial extends ArmorMaterial
{
    /**
     * Returns the crushing modifier this armor has
     *
     * @return float value with the modifier. To check how damage calculation is done, see {@link DamageType}
     */
    float getCrushingModifier();

    /**
     * Returns the crushing modifier this armor has
     *
     * @return float value with the modifier. To check how damage calculation is done, see {@link DamageType}
     */
    float getPiercingModifier();

    /**
     * Returns the crushing modifier this armor has
     *
     * @return float value with the modifier. To check how damage calculation is done, see {@link DamageType}
     */
    float getSlashingModifier();

    ResourceLocation getLocation();

    /**
     * For our own armors, we don't let them be repaired. Addon armors could override this if they wanted.
     */
    @Override
    default Ingredient getRepairIngredient()
    {
        return Ingredient.EMPTY;
    }
}