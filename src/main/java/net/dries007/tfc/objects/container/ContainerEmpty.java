/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.objects.container;

import javax.annotation.Nullable;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;

public class ContainerEmpty extends Container
{
    @Override
    public void onCraftMatrixChanged(IInventory inventory)
    {
    }

    @Override
    public boolean canInteractWith(@Nullable EntityPlayer player)
    {
        return false;
    }
}
