/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.mixin.item;

import net.minecraft.item.Item;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

/**
 * Used to modify stack sizes on items for {@link net.dries007.tfc.common.capabilities.size.IItemSize}
 */
@Mixin(Item.class)
public interface ItemAccessor
{
    @Accessor(value = "maxStackSize")
    void accessor$setMaxStackSize(int maxStackSize);
}
