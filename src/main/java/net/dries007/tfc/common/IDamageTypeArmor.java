/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common;

/**
 * @see TFCArmorMaterial
 */
public interface IDamageTypeArmor
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
}
