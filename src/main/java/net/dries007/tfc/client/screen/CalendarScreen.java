/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.fml.network.PacketDistributor;

import com.mojang.blaze3d.matrix.MatrixStack;
import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.container.SimpleContainer;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Month;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class CalendarScreen extends TFCContainerScreen<SimpleContainer>
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/player_calendar.png");

    public CalendarScreen(SimpleContainer container, PlayerInventory playerInv, ITextComponent name)
    {
        super(container, playerInv, name, BACKGROUND);
    }

    @Override
    public void init()
    {
        super.init();
        addButton(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 4, 20, 22, 128, 0, 1, 3, 0, 0, button -> {
            playerInventory.player.openContainer = playerInventory.player.container;
            Minecraft.getInstance().displayGuiScreen(new InventoryScreen(playerInventory.player));
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new SwitchInventoryTabPacket(SwitchInventoryTabPacket.Type.INVENTORY));
        }));
        addButton(new PlayerInventoryTabButton(guiLeft, guiTop, 176 - 3, 27, 20 + 3, 22, 128 + 20, 0, 1, 3, 32, 0, button -> {}));
        addButton(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 50, 20, 22, 128, 0, 1, 3, 64, 0, SwitchInventoryTabPacket.Type.NUTRITION));
        addButton(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 73, 20, 22, 128, 0, 1, 3, 96, 0, SwitchInventoryTabPacket.Type.CLIMATE));
    }

    @Override
    protected void renderHoveredTooltip(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        super.renderHoveredTooltip(matrixStack, mouseX, mouseY);

        String season = I18n.format("tfc.tooltip.calendar_season") + I18n.format(Calendars.CLIENT.getCalendarMonthOfYear().getTranslationKey(Month.Style.SEASON));
        String day = I18n.format("tfc.tooltip.calendar_day") + Calendars.CLIENT.getCalendarDayOfYear().getString();
        String date = I18n.format("tfc.tooltip.calendar_date") + Calendars.CLIENT.getCalendarTimeAndDate().getString();

        font.drawString(matrixStack, season, (width - font.getStringWidth(season)) / 2f, 25, 0x404040);
        font.drawString(matrixStack, day, (width - font.getStringWidth(day)) / 2f, 34, 0x404040);
        font.drawString(matrixStack, date, (width - font.getStringWidth(date)) / 2f, 43, 0x404040);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(MatrixStack matrixStack, float partialTicks, int x, int y) {

    }
}