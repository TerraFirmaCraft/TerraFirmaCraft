package net.dries007.tfc.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import net.dries007.tfc.common.container.SmallVesselInventoryContainer;

public class SmallVesselInventoryScreen extends TFCContainerScreen<SmallVesselInventoryContainer>
{
    public SmallVesselInventoryScreen(SmallVesselInventoryContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, INVENTORY_2x2);
    }
}
