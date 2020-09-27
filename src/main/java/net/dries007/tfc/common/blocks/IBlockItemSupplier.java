/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.common.blocks;

import java.util.function.Function;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

/**
 * Fancy interface for special casing BlockItem, used in registration
 */
public interface IBlockItemSupplier extends Function<Item.Properties, BlockItem>
{
}
