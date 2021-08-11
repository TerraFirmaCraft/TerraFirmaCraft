/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.network.chat.Component;

import net.dries007.tfc.common.container.TileEntityContainer;
import net.dries007.tfc.common.tileentity.InventoryTileEntity;

public class TileEntityScreen<T extends InventoryTileEntity<?>, C extends TileEntityContainer<T>> extends TFCContainerScreen<C>
{
    protected final T tile;

    public TileEntityScreen(C container, Inventory playerInventory, Component name, ResourceLocation texture)
    {
        super(container, playerInventory, name, texture);
        this.tile = container.getTileEntity();
    }
}
