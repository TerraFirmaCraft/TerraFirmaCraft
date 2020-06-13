/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.blocks;

import java.util.function.Function;

import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;

/**
 * Fancy interface for special casing BlockItem for registration
 */
public interface IBlockItemSupplier extends Function<Item.Properties, BlockItem>
{
}
