/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class GuiSalad extends GuiContainerTFC
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/salad.png");

    public GuiSalad(Container container, InventoryPlayer playerInv)
    {
        super(container, playerInv, BACKGROUND);
    }
}
