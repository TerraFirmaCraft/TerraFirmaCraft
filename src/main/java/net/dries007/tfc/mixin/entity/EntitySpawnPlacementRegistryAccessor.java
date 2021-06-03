/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.entity;

import java.util.Map;

import net.minecraft.entity.EntitySpawnPlacementRegistry;
import net.minecraft.entity.EntityType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntitySpawnPlacementRegistry.class)
public interface EntitySpawnPlacementRegistryAccessor
{
    @Accessor("DATA_BY_TYPE")
    static Map<EntityType<?>, ?> getPlacementMap()
    {
        throw new UnsupportedOperationException();
    };
}
