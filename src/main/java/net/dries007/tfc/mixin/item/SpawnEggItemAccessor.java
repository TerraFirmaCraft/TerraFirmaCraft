/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

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
