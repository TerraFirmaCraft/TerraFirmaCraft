/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities;

import net.minecraft.world.entity.LivingEntity;

public interface Scareable
{
    default boolean isScared()
    {
        return false;
    }

    default boolean isScaredBy(LivingEntity entity)
    {
        return false;
    }

    default boolean isCurrentlyScareable()
    {
        return true;
    }
}
