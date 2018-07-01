/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items.metal;

import javax.annotation.ParametersAreNonnullByDefault;

import mcp.MethodsReturnNonnullByDefault;
import net.dries007.tfc.objects.Metal;
import net.dries007.tfc.util.IPlacableItem;

@MethodsReturnNonnullByDefault
@ParametersAreNonnullByDefault
public class ItemIngot extends ItemMetal implements IPlacableItem
{
    public ItemIngot(Metal metal, Metal.ItemType type)
    {
        super(metal, type);
    }
}
