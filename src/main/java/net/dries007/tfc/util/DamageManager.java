/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.util;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.DamageSource;

public final class DamageManager
{
    /**
     * Reescale damage, from base max health(ie: 20 for vanilla) to max health(ie: 1000 for TFC Classic)
     * @param value to be scaled
     * @param baseMaxHealth for base max health(one hit would kill)
     * @param toMaxHealth for the rescaled max health
     * @return
     */
    public static float rescaleDamage(float value, float baseMaxHealth, float toMaxHealth)
    {
        return value * toMaxHealth / baseMaxHealth;
    }

    /**
     * Apply armor defence on damage.
     * @param originalDamage the incoming damage(should be scaled)
     * @param source the DamageSource from LivingHurtEvent
     * @param player the player from LivingHurtEvent
     * @return
     */
    public static float applyArmor(float originalDamage, DamageSource source, EntityPlayer player)
    {
        //TODO Not yet implemented
        return originalDamage;
    }
}
