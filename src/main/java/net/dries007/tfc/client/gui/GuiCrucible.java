/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import java.util.Map;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;

import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.te.TECrucible;
import net.dries007.tfc.util.Alloy;

import static net.dries007.tfc.api.capability.heat.CapabilityItemHeat.MAX_TEMPERATURE;
import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class GuiCrucible extends GuiContainerTE<TECrucible>
{
    private static final ResourceLocation CRUCIBLE_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/crucible.png");

    public GuiCrucible(Container container, InventoryPlayer playerInv, TECrucible tile)
    {
        super(container, playerInv, tile, CRUCIBLE_BACKGROUND);

        this.ySize = 192;
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY)
    {
        if (mouseX > guiLeft + 128 && mouseX < guiLeft + 137 && mouseY > guiTop + 5 && mouseY < guiTop + 107)
        {
            int amount = tile.getAlloy().getAmount();
            drawHoveringText(I18n.format(MOD_ID + ".tooltip.units", amount), mouseX, mouseY);
        }
        super.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        // Draw the temperature indicator
        int temperature = (int) (51 * tile.getField(TECrucible.FIELD_TEMPERATURE) / MAX_TEMPERATURE);
        if (temperature > 0)
        {
            drawTexturedModalRect(guiLeft + 153, guiTop + 80 - temperature, 176, 0, 15, 5);
        }

        // Draw the filled amount
        Alloy alloy = tile.getAlloy();
        if (alloy.getAmount() > 0)
        {
            int fill = (int) (99f * alloy.getAmount() / TECrucible.CRUCIBLE_MAX_METAL_FLUID);
            drawTexturedModalRect(guiLeft + 129, guiTop + 106 - fill, 191, 0, 8, fill);

            // Draw Title:
            Metal result = tile.getAlloyResult();
            String resultText = TextFormatting.UNDERLINE + I18n.format(result.getTranslationKey()) + ":";
            fontRenderer.drawString(resultText, guiLeft + 7, guiTop + 7, 0x000000);

            // Draw Components
            int yPos = guiTop + 18;
            for (Map.Entry<Metal, Double> entry : alloy.getMetals().entrySet())
            {
                String metalName = I18n.format(entry.getKey().getTranslationKey());
                String displayText = String.format("%s: %s%2.2f%%", metalName, TextFormatting.DARK_GREEN, 100 * entry.getValue() / alloy.getAmount());
                fontRenderer.drawString(displayText, guiLeft + 7, yPos, 0x404040);
                yPos += 9;
            }
        }
    }
}
