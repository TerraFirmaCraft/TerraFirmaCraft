package net.dries007.tfc.client.screen;

import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;

import net.dries007.tfc.common.container.TileEntityContainer;
import net.dries007.tfc.common.tileentity.InventoryTileEntity;

public class TileEntityScreen<T extends InventoryTileEntity, C extends TileEntityContainer<T>> extends TFCContainerScreen<C>
{
    protected final InventoryTileEntity tile;

    public TileEntityScreen(C container, PlayerInventory playerInventory, ITextComponent name, ResourceLocation texture)
    {
        super(container, playerInventory, name, texture);
        this.tile = container.getTileEntity();
    }
}
