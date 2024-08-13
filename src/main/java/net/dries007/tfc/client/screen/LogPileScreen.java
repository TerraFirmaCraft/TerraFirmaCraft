/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;

import net.dries007.tfc.common.container.LogPileContainer;
import net.dries007.tfc.util.Helpers;

public class LogPileScreen extends TFCContainerScreen<LogPileContainer>
{
    public LogPileScreen(LogPileContainer container, Inventory playerInventory, Component name)
    {
        super(container, playerInventory, name, Helpers.identifier("textures/gui/log_pile.png"));
        inventoryLabelY += 20;
        imageHeight += 20;
    }

}
