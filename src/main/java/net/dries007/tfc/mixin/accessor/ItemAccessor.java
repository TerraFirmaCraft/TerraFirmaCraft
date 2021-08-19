package net.dries007.tfc.mixin.accessor;

import net.minecraft.world.item.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface ItemAccessor
{
    @Mutable
    @Accessor("maxStackSize")
    void accessor$setMaxStackSize(int maxStackSize);
}
