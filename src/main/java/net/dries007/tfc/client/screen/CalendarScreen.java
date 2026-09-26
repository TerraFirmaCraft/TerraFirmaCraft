/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.CreativeModeInventoryScreen;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.container.Container;
import net.dries007.tfc.compat.patchouli.PatchouliIntegration;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;
import net.dries007.tfc.util.calendar.Month;

import static net.dries007.tfc.client.screen.TFCContainerScreen.TextAlignment.*;

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
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, false, false, PlayerInventoryTabButton.Tab.INVENTORY, button -> {
            playerInventory.player.containerMenu = playerInventory.player.inventoryMenu;
            Minecraft mc = Minecraft.getInstance();
            if (mc.gameMode != null && mc.gameMode.isServerControlledInventory() && mc.player != null && TFCConfig.CLIENT.enableTabsInCreative.get())
            {
                mc.setScreen(new CreativeModeInventoryScreen((LocalPlayer) playerInventory.player, mc.player.connection.enabledFeatures(), mc.options.operatorItemsTab().get()));
            }
            else
            {
                mc.setScreen(new InventoryScreen(playerInventory.player));
            }
            PacketDistributor.sendToServer(new SwitchInventoryTabPacket(PlayerInventoryTabButton.Tab.INVENTORY));
        }));
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, true, false, PlayerInventoryTabButton.Tab.CALENDAR, button -> {}));
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, false, false, PlayerInventoryTabButton.Tab.NUTRITION));
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, false, false, PlayerInventoryTabButton.Tab.CLIMATE));
        PatchouliIntegration.ifEnabled(() -> addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, false, false, PlayerInventoryTabButton.Tab.BOOK)));
    }

    @Override
    protected void renderLabels(GuiGraphics graphics, int mouseX, int mouseY)
    {
        super.renderLabels(graphics, mouseX, mouseY);

        String date =
            Calendars.CLIENT.getCalendarDayOfYear().getString() + ", " +
                I18n.get(Calendars.CLIENT.getAbsoluteCalendarMonthOfYear().getTranslationKey(Month.Style.LONG_MONTH)) + " " +
                Calendars.CLIENT.getCalendarDayOfMonth() + ", " + Calendars.CLIENT.getCalendarYear();

        // this is kind of unnecessary with the default font, but it can potentially make the time look nicer with other fonts
        drawLine(graphics, Component.literal(":"), CENTER, 22);
        drawLine(graphics, Component.literal(String.format("%02d", Calendars.CLIENT.getHourOfDay())), RIGHT, -1, 83, 22);
        drawLine(graphics, Component.literal(String.format("%02d", Calendars.CLIENT.getMinuteOfHour())), LEFT, -1, 83, 22);

        drawLine(graphics, Component.translatable(date), CENTER, 33);

        if (Calendars.CLIENT.getBirthday().equals(Component.empty()))
        {
            int daysLeft = Calendars.CLIENT.getCalendarDaysInMonth() - Calendars.CLIENT.getCalendarDayOfMonth();
            String daysLeftKey = "tfc.tooltip.calendar_days_left_in_month";

            if (daysLeft == 1)
            {
                daysLeftKey = "tfc.tooltip.calendar_second_last_day_in_month";
            }
            if (daysLeft == 0)
            {
                daysLeftKey = "tfc.tooltip.calendar_last_day_in_month";
                drawLine(graphics, Component.translatable(daysLeftKey, I18n.get(Calendars.CLIENT.getAbsoluteCalendarMonthOfYear().getTranslationKey(Month.Style.LONG_MONTH))), CENTER, 44);
            }
            else
            {
                drawLine(graphics, Component.translatable(daysLeftKey, daysLeft, I18n.get(Calendars.CLIENT.getAbsoluteCalendarMonthOfYear().getTranslationKey(Month.Style.LONG_MONTH))), CENTER, 44);
            }

        }
        else
        {
            drawLine(graphics, Calendars.CLIENT.getBirthday(), CENTER, 44);
        }

        drawLine(graphics, Component.translatable(Calendars.CLIENT.getHemispheralCalendarMonthOfYear(ClientHelpers.inNorthernHemisphere()).getTranslationKey(Month.Style.SEASON)), CENTER, 55);

        if (Calendars.CLIENT.getHourOfDay() >= 20 || Calendars.CLIENT.getHourOfDay() < 5)
        {
            graphics.blit(texture, 154, 3, 176, 0, 16, 16);
        }
    }
}