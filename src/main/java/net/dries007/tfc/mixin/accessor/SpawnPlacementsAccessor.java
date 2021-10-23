/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.accessor;

import java.util.Map;

import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.SpawnPlacements;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpawnPlacements.class)
public interface SpawnPlacementsAccessor
{
    @Accessor("DATA_BY_TYPE")
    static Map<EntityType<?>, ?> accessor$getSpawnData()
    {
        throw new AssertionError();
    }
}
