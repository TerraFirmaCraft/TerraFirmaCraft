/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock.horse;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.horse.AbstractHorse;

import net.dries007.tfc.common.entities.livestock.MammalProperties;
import net.dries007.tfc.util.calendar.Calendars;
import org.jetbrains.annotations.Nullable;

public interface HorseProperties extends MammalProperties
{
    @Nullable
    @SuppressWarnings("unchecked")
    default AgeableMob createBabyHorse(ServerLevel level)
    {
        // todo: read the genes here!
        AbstractHorse baby = ((EntityType<AbstractHorse>) getEntity().getType()).create(level);
        if (baby instanceof HorseProperties properties)
        {
            properties.setGender(Gender.valueOf(getEntity().getRandom().nextBoolean()));
            properties.setBirthDay((int) Calendars.SERVER.getTotalDays());
            properties.setFamiliarity(this.getFamiliarity() < 0.9F ? this.getFamiliarity() / 2.0F : this.getFamiliarity() * 0.9F);
            return baby;
        }
        return null;
    }
}
