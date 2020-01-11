package net.dries007.tfc.objects.container;

/*
 * Part of the AlcatrazCore mod by AlcatrazEscapee
 * Work under Copyright. Licensed under the GPL-3.0.
 * See the project LICENSE.md for more information.
 */

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
