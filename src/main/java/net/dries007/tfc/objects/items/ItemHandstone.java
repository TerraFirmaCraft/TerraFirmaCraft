/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.items;

import net.dries007.tfc.api.capability.size.Size;
import net.dries007.tfc.api.capability.size.Weight;

public class ItemHandstone extends ItemCraftingTool
{
    ItemHandstone()
    {
        super(250, Size.NORMAL, Weight.HEAVY, "handstone");
    }
}
