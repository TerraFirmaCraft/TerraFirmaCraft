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
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class GuiQuern extends GuiContainerTE<TEQuern>
{
    private static final ResourceLocation QUERN_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/quern.png");
    private final String translationKey;

    public GuiQuern(Container container, InventoryPlayer playerInv, TEQuern tile, String translationKey)
    {
        super(container, playerInv, tile, QUERN_BACKGROUND);

        this.translationKey = translationKey;
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        String name = I18n.format(translationKey + ".name");
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040);
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        if (Helpers.isJEIEnabled())
        {
            drawTexturedModalRect(guiLeft + 114, guiTop + 48, 176, 0, 9, 14);
        }
    }
}
