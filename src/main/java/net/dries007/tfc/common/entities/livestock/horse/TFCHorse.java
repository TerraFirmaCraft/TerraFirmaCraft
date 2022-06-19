/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock.horse;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.level.Level;

public class TFCHorse extends Horse
{
    public TFCHorse(EntityType<? extends Horse> type, Level level)
    {
        super(type, level);
    }
}
