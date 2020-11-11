package net.dries007.tfc.mixin.entity;

import net.minecraft.entity.EntityType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityType.class)
public interface EntityTypeAccessor
{
    /**
     * This is used to avoid a stupid log message during entity type registration
     */
    @Accessor("serialize")
    void accessor$setSerialize(boolean serialize);
}
