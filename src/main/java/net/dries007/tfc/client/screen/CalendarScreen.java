/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.container.Container;
import net.dries007.tfc.compat.patchouli.PatchouliIntegration;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Month;

public class CalendarScreen extends TFCContainerScreen<Container>
{
    public static final ResourceLocation BACKGROUND = Helpers.identifier("textures/gui/player_calendar.png");

    public CalendarScreen(Container container, Inventory playerInv, Component name)
    {
        super(container, playerInv, name, BACKGROUND);
    }

    @Override
    public void init()
    {
        super.init();
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, 176, 4, 20, 22, 128, 0, 1, 3, 0, 0, button -> {
            playerInventory.player.containerMenu = playerInventory.player.inventoryMenu;
            Minecraft.getInstance().setScreen(new InventoryScreen(playerInventory.player));
            PacketDistributor.sendToServer(new SwitchInventoryTabPacket(SwitchInventoryTabPacket.Tab.INVENTORY));
        }));
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, 176 - 3, 27, 20 + 3, 22, 128 + 20, 0, 1, 3, 32, 0, button -> {}));
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, 176, 50, 20, 22, 128, 0, 1, 3, 64, 0, SwitchInventoryTabPacket.Tab.NUTRITION));
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, 176, 73, 20, 22, 128, 0, 1, 3, 96, 0, SwitchInventoryTabPacket.Tab.CLIMATE));
        PatchouliIntegration.ifEnabled(() -> addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, 176, 96, 20, 22, 128, 0, 1, 3, 0, 32, SwitchInventoryTabPacket.Tab.BOOK)));
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderLabels(graphics, mouseX, mouseY);

        String season = I18n.get("tfc.tooltip.calendar_season", I18n.get(Calendars.CLIENT.getCalendarMonthOfYear().getTranslationKey(Month.Style.SEASON)));
        String day = I18n.get("tfc.tooltip.calendar_day", Calendars.CLIENT.getCalendarDayOfYear().getString());
        String date = I18n.get("tfc.tooltip.calendar_date", Calendars.CLIENT.getTimeAndDate().getString());

        graphics.drawString(font, season, (imageWidth - font.width(season)) / 2, 25, 0x404040, false);
        graphics.drawString(font, day, (imageWidth - font.width(day)) / 2, 36, 0x404040, false);
        graphics.drawString(font, date, (imageWidth - font.width(date)) / 2, 47, 0x404040, false);
    }
}