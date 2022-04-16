/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import net.dries007.tfc.common.container.NestBoxContainer;

public class NestBoxScreen extends TFCContainerScreen<NestBoxContainer>
{
    public NestBoxScreen(NestBoxContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, INVENTORY_2x2);
    }
}
