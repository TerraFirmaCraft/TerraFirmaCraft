/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.objects.te.TEQuern;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class GuiQuern extends GuiContainerTE<TEQuern>
{
    private static final ResourceLocation QUERN_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/quern.png");

    public GuiQuern(Container container, InventoryPlayer playerInv, TEQuern tile)
    {
        super(container, playerInv, tile, QUERN_BACKGROUND);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        this.fontRenderer.drawString(I18n.format("container." + MOD_ID + ".quern"), 80, 6, 4210752);
        this.fontRenderer.drawString(I18n.format("container.inventory"), 8, this.ySize - 96 + 2, 4210752);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
    }
}
