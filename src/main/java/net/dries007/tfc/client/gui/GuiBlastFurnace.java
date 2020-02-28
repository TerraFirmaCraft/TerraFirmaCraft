/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.capability.heat.Heat;
import net.dries007.tfc.objects.te.TEBlastFurnace;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
public class GuiBlastFurnace extends GuiContainerTE<TEBlastFurnace>
{
    private static final ResourceLocation BLAST_FURNACE_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/blast_furnace.png");

    public GuiBlastFurnace(Container container, InventoryPlayer playerInv, TEBlastFurnace tile)
    {
        super(container, playerInv, tile, BLAST_FURNACE_BACKGROUND);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        int temperature = (int) (51 * tile.getField(TEBlastFurnace.FIELD_TEMPERATURE) / Heat.maxVisibleTemperature());
        if (temperature > 0)
        {
            if (temperature > 51)
            {
                temperature = 51;
            }
            drawTexturedModalRect(guiLeft + 8, guiTop + 66 - temperature, 185, 32, 15, 5);
        }

        int oreCount = tile.getField(TEBlastFurnace.FIELD_ORE) * 4;
        if (oreCount > 0)
        {
            drawTexturedModalRect(guiLeft + 40, guiTop + 25, 176, 0, oreCount + 1, 8);
        }

        int fuelCount = tile.getField(TEBlastFurnace.FIELD_FUEL) * 4;
        if (fuelCount > 0)
        {
            drawTexturedModalRect(guiLeft + 40, guiTop + 43, 176, 0, fuelCount + 1, 8);
        }

        int meltAmount = tile.getField(TEBlastFurnace.FIELD_MELT);
        if (meltAmount > 700) meltAmount = 700;
        meltAmount = (int) (meltAmount / 8.75f);
        if (meltAmount > 0)
        {
            drawTexturedModalRect(guiLeft + 40, guiTop + 61, 176, 0, meltAmount + 1, 8);
        }

        fontRenderer.drawString(I18n.format(MOD_ID + ".tooltip.blast_furnace_ore_amount"), guiLeft + 40, guiTop + 17, 0x000000);
        fontRenderer.drawString(I18n.format(MOD_ID + ".tooltip.blast_furnace_fuel_amount"), guiLeft + 40, guiTop + 35, 0x000000);
        fontRenderer.drawString(I18n.format(MOD_ID + ".tooltip.blast_furnace_melt_amount"), guiLeft + 40, guiTop + 53, 0x000000);

        if (tile.getField(TEBlastFurnace.CHIMNEY_LEVELS) < 1)
        {
            fontRenderer.drawString(I18n.format(MOD_ID + ".tooltip.blast_furnace_invalid_structure"), guiLeft + 40, guiTop + 71, 0xDC2400);
        }
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY)
    {
        if (mouseX > guiLeft + 40 && mouseX < guiLeft + 120 && mouseY > guiTop + 25 && mouseY < guiTop + 33)
        {
            int amount = tile.getField(TEBlastFurnace.FIELD_ORE_UNITS);
            drawHoveringText(I18n.format(MOD_ID + ".tooltip.units", amount), mouseX, mouseY);
        }
        if (mouseX > guiLeft + 40 && mouseX < guiLeft + 120 && mouseY > guiTop + 43 && mouseY < guiTop + 51)
        {
            int amount = tile.getField(TEBlastFurnace.FIELD_FUEL);
            drawHoveringText(I18n.format(MOD_ID + ".tooltip.units", amount), mouseX, mouseY);
        }
        if (mouseX > guiLeft + 40 && mouseX < guiLeft + 120 && mouseY > guiTop + 61 && mouseY < guiTop + 69)
        {
            int amount = tile.getField(TEBlastFurnace.FIELD_MELT);
            drawHoveringText(I18n.format(MOD_ID + ".tooltip.units", amount), mouseX, mouseY);
        }
        super.renderHoveredToolTip(mouseX, mouseY);
    }
}
