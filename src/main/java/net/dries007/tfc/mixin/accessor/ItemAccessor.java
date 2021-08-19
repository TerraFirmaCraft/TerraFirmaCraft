package net.dries007.tfc.mixin.accessor;

import net.minecraft.world.item.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(Item.class)
public interface ItemAccessor
{
    @Accessor("maxStackSize")
    void accessor$setMaxStackSize(int maxStackSize);
}
