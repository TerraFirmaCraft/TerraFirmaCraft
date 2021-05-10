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
