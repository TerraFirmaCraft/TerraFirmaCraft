package net.dries007.tfc.mixin.entity;

import net.minecraft.entity.EntityType;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(EntityType.class)
public interface EntityTypeAccessor
{
    @Accessor("serialize")
    void accessor$setSerialize(boolean serialize);
}
