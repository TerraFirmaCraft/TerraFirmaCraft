/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;

public class GuiContainerTE<T extends TileEntity> extends GuiContainerTFC
{
    protected final T tile;

    public GuiContainerTE(Container container, InventoryPlayer playerInv, T tile, ResourceLocation background)
    {
        super(container, playerInv, background);

        this.tile = tile;
    }
}
