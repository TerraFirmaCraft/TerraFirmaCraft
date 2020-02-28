/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextFormatting;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.client.button.GuiButtonPlayerInventoryTab;
import net.dries007.tfc.network.PacketSwitchPlayerInventoryTab;
import net.dries007.tfc.util.calendar.CalendarTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
public class GuiCalendar extends GuiContainerTFC
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/player_calendar.png");

    public GuiCalendar(Container container, InventoryPlayer playerInv)
    {
        super(container, playerInv, BACKGROUND);
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int buttonId = 0;
        addButton(new GuiButtonPlayerInventoryTab(TFCGuiHandler.Type.INVENTORY, guiLeft, guiTop, ++buttonId, true));
        addButton(new GuiButtonPlayerInventoryTab(TFCGuiHandler.Type.SKILLS, guiLeft, guiTop, ++buttonId, true));
        addButton(new GuiButtonPlayerInventoryTab(TFCGuiHandler.Type.CALENDAR, guiLeft, guiTop, ++buttonId, false));
        addButton(new GuiButtonPlayerInventoryTab(TFCGuiHandler.Type.NUTRITION, guiLeft, guiTop, ++buttonId, true));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        String tooltip = TextFormatting.WHITE + "" + TextFormatting.UNDERLINE + I18n.format("tfc.tooltip.calendar") + ":";
        fontRenderer.drawString(tooltip, xSize / 2 - fontRenderer.getStringWidth(tooltip) / 2, 7, 0x404040);

        String season, day, date;

        season = I18n.format("tfc.tooltip.season", CalendarTFC.CALENDAR_TIME.getSeasonDisplayName());
        day = I18n.format("tfc.tooltip.day", CalendarTFC.CALENDAR_TIME.getDisplayDayName());
        date = I18n.format("tfc.tooltip.date", CalendarTFC.CALENDAR_TIME.getTimeAndDate());

        fontRenderer.drawString(season, xSize / 2 - fontRenderer.getStringWidth(season) / 2, 25, 0x404040);
        fontRenderer.drawString(day, xSize / 2 - fontRenderer.getStringWidth(day) / 2, 34, 0x404040);
        fontRenderer.drawString(date, xSize / 2 - fontRenderer.getStringWidth(date) / 2, 43, 0x404040);
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button instanceof GuiButtonPlayerInventoryTab && ((GuiButtonPlayerInventoryTab) button).isActive())
        {
            GuiButtonPlayerInventoryTab tabButton = (GuiButtonPlayerInventoryTab) button;
            if (tabButton.isActive())
            {
                if (tabButton.getGuiType() == TFCGuiHandler.Type.INVENTORY)
                {
                    this.mc.displayGuiScreen(new GuiInventory(playerInv.player));
                }
                TerraFirmaCraft.getNetwork().sendToServer(new PacketSwitchPlayerInventoryTab(tabButton.getGuiType()));
            }
        }
    }
}
