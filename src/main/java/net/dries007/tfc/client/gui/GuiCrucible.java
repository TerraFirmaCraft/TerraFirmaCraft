package net.dries007.tfc.client.gui;

import java.util.Map;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.api.types.Metal;
import net.dries007.tfc.objects.te.TECrucible;
import net.dries007.tfc.objects.te.TEFirePit;
import net.dries007.tfc.util.Alloy;

import static net.dries007.tfc.api.capability.heat.CapabilityItemHeat.MAX_TEMPERATURE;
import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class GuiCrucible extends GuiContainerTE<TECrucible>
{
    private static final ResourceLocation CRUCIBLE_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/crucible.png");

    public GuiCrucible(Container container, InventoryPlayer playerInv, TECrucible tile)
    {
        super(container, playerInv, tile, CRUCIBLE_BACKGROUND);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        // Draw the fire / burn time indicator
        int temperature = (int) (51 * tile.getField(TEFirePit.FIELD_TEMPERATURE) / MAX_TEMPERATURE);
        if (temperature > 0)
        {
            drawTexturedModalRect(guiLeft + 30, guiTop + 16 - temperature, 176, 0, 15, 5);
        }

        Alloy alloy = tile.getAlloy();
        // Draw Title:
        Metal result = alloy.getResult();
        String resultText = I18n.format(MOD_ID + ".tooltip.crucible_result") + I18n.format(result.getTranslationKey());
        fontRenderer.drawString(resultText, guiLeft + 50 - fontRenderer.getStringWidth(resultText) / 2, guiTop + 6, 0x404040);

        // Draw Components
        int offset = 0;
        for (Map.Entry<Metal, Double> entry : alloy.getMetals().entrySet())
        {
            String metalName = I18n.format(entry.getKey().getTranslationKey());
            String displayText = String.format("%s: %2.2f%%", metalName, 100 * entry.getValue() / alloy.getAmount());
            fontRenderer.drawString(displayText, guiLeft + 50 - fontRenderer.getStringWidth(displayText) / 2, guiTop + 20 + offset, 0x404040);
            offset += 16;
        }
    }
}
