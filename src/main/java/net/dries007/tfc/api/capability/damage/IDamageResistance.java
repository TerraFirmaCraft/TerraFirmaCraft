/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.api.capability.damage;

public interface IDamageResistance
{
    default float getCrushingModifier()
    {
        return 1;
    }

    default float getPiercingModifier()
    {
        return 1;
    }

    default float getSlashingModifier()
    {
        return 1;
    }
}
