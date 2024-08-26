/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.container.Container;
import net.dries007.tfc.compat.patchouli.PatchouliIntegration;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.config.TemperatureDisplayStyle;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.KoppenClimateClassification;

public class ClimateScreen extends TFCContainerScreen<Container>
{
    public static final ResourceLocation BACKGROUND = Helpers.identifier("textures/gui/player_climate.png");

    public ClimateScreen(Container container, Inventory playerInv, Component name)
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
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, 176, 27, 20, 22, 128, 0, 1, 3, 32, 0, SwitchInventoryTabPacket.Tab.CALENDAR));
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, 176, 50, 20, 22, 128, 0, 1, 3, 64, 0, SwitchInventoryTabPacket.Tab.NUTRITION));
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, 176 - 3, 73, 20 + 3, 22, 128 + 20, 0, 1, 3, 96, 0, button -> {}));
        PatchouliIntegration.ifEnabled(() -> addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, 176, 96, 20, 22, 128, 0, 1, 3, 0, 32, SwitchInventoryTabPacket.Tab.BOOK)));
    }

    @Override
    protected void renderLabels(GuiGraphics stack, int mouseX, int mouseY)
    {
        super.renderLabels(stack, mouseX, mouseY);

        // Climate at the current player
        final float averageTemp = ClimateRenderCache.INSTANCE.getAverageTemperature();
        final float rainfall = ClimateRenderCache.INSTANCE.getAverageRainfall();
        final float rainVar = ClimateRenderCache.INSTANCE.getRainVariance();
        final float currentTemp = ClimateRenderCache.INSTANCE.getTemperature();

        final TemperatureDisplayStyle style = TFCConfig.CLIENT.climateTooltipStyle.get();

        drawCenteredLine(stack, Component.translatable("tfc.tooltip.climate_koppen_climate_classification", Helpers.translateEnum(KoppenClimateClassification.classify(averageTemp, rainfall, rainVar))), 17);
        drawCenteredLine(stack, Component.translatable("tfc.tooltip.climate_average_temperature", style.format(averageTemp, true)), 28);
        drawCenteredLine(stack, Component.translatable("tfc.tooltip.climate_annual_rainfall", String.format("%.0f", rainfall)), 39);
        drawCenteredLine(stack, Component.translatable(rainVar > 0 ? "tfc.tooltip.climate_peak_rainfall_summer" : "tfc.tooltip.climate_peak_rainfall_winter", String.format("%.0f", rainfall * (1 + Math.abs(rainVar)))), 50);
        drawCenteredLine(stack, Component.translatable("tfc.tooltip.climate_current_temp", style.format(currentTemp, true)), 61);
    }

}