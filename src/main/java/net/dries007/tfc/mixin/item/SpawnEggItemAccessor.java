package net.dries007.tfc.mixin.item;

import java.util.Map;

import net.minecraft.entity.EntityType;
import net.minecraft.item.SpawnEggItem;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(SpawnEggItem.class)
public interface SpawnEggItemAccessor
{
    @Accessor("BY_ID")
    static Map<EntityType<?>, SpawnEggItem> accessor$getIdMap()
    {
        throw new UnsupportedOperationException();
    }
}
