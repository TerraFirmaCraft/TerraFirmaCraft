/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.common.component.item;

import java.util.List;
import net.minecraft.world.item.ItemStack;

/**
 * An immutable representation of an item container, containing a fixed amount of slots. Contents are restricted by
 * the given slot count, slot capacity, and predicate.
 */
public record ItemComponent(List<ItemStack> contents)
{
}
