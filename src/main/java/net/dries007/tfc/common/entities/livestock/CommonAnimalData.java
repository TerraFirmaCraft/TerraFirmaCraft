/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.entities.livestock;

import net.minecraft.network.syncher.EntityDataAccessor;

public record CommonAnimalData(EntityDataAccessor<Boolean> gender, EntityDataAccessor<Long> birthday, EntityDataAccessor<Float> familiarity, EntityDataAccessor<Integer> uses, EntityDataAccessor<Boolean> fertilized, EntityDataAccessor<Long> oldDay, EntityDataAccessor<Integer> geneticSize)
{
}
