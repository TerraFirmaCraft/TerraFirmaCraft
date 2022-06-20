package net.dries007.tfc.mixin.accessor;

import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.animal.horse.Markings;
import net.minecraft.world.entity.animal.horse.Variant;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(Horse.class)
public interface HorseAccessor
{
    @Invoker("setVariantAndMarkings")
    void invoke$setVariantAndTypeMarkings(Variant variant, Markings markings);
}
