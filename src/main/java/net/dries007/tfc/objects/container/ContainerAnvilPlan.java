/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import net.minecraft.entity.player.InventoryPlayer;

import net.dries007.tfc.objects.te.TEAnvilTFC;

public class ContainerAnvilPlan extends ContainerTE<TEAnvilTFC>
{
    public ContainerAnvilPlan(InventoryPlayer playerInv, TEAnvilTFC tile)
    {
        super(playerInv, tile, true);
    }

    @Override
    protected void addContainerSlots()
    {
    }
}
