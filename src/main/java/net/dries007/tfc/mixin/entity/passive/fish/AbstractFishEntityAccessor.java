package net.dries007.tfc.mixin.entity.passive.fish;

import net.minecraft.entity.passive.fish.AbstractFishEntity;
import net.minecraft.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(AbstractFishEntity.class)
public interface AbstractFishEntityAccessor
{
    @Invoker("saveToBucketTag")
    void invoke$saveToBucketTag(ItemStack stack);
}
