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
        addButton(new PlayerInventoryTabButton(leftPos, topPos, 176, 4, 20, 22, 128, 0, 1, 3, 0, 0, button -> {
            inventory.player.containerMenu = inventory.player.inventoryMenu;
            Minecraft.getInstance().setScreen(new InventoryScreen(inventory.player));
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new SwitchInventoryTabPacket(SwitchInventoryTabPacket.Type.INVENTORY));
        }));
        addButton(new PlayerInventoryTabButton(leftPos, topPos, 176 - 3, 27, 20 + 3, 22, 128 + 20, 0, 1, 3, 32, 0, button -> {}));
        addButton(new PlayerInventoryTabButton(leftPos, topPos, 176, 50, 20, 22, 128, 0, 1, 3, 64, 0, SwitchInventoryTabPacket.Type.NUTRITION));
        addButton(new PlayerInventoryTabButton(leftPos, topPos, 176, 73, 20, 22, 128, 0, 1, 3, 96, 0, SwitchInventoryTabPacket.Type.CLIMATE));
    }

    @Override
    protected void renderLabels(MatrixStack stack, int mouseX, int mouseY)
    {
        super.renderLabels(stack, mouseX, mouseY);

        String season = I18n.get("tfc.tooltip.calendar_season") + I18n.get(Calendars.CLIENT.getCalendarMonthOfYear().getTranslationKey(Month.Style.SEASON));
        String day = I18n.get("tfc.tooltip.calendar_day") + Calendars.CLIENT.getCalendarDayOfYear().getString();
        String date = I18n.get("tfc.tooltip.calendar_date") + Calendars.CLIENT.getCalendarTimeAndDate().getString();

        font.draw(stack, season, (imageWidth - font.width(season)) / 2f, 25, 0x404040);
        font.draw(stack, day, (imageWidth - font.width(day)) / 2f, 34, 0x404040);
        font.draw(stack, date, (imageWidth - font.width(date)) / 2f, 43, 0x404040);
    }
}