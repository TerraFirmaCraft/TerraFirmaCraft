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
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.neoforged.neoforge.network.PacketDistributor;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.container.Container;
import net.dries007.tfc.compat.patchouli.PatchouliIntegration;
import net.dries007.tfc.config.TFCConfig;
import net.dries007.tfc.config.TemperatureDisplayStyle;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.KoppenClimateClassification;

import static net.dries007.tfc.client.screen.TFCContainerScreen.TextAlignment.*;

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
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, false, false, PlayerInventoryTabButton.Tab.CALENDAR));
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, false, false, PlayerInventoryTabButton.Tab.NUTRITION));
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, true, false, PlayerInventoryTabButton.Tab.CLIMATE, button -> {}));
        PatchouliIntegration.ifEnabled(() -> addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, false, false, PlayerInventoryTabButton.Tab.BOOK)));
    }

    @Override
    protected void renderLabels(GuiGraphics stack, int mouseX, int mouseY)
    {
        super.renderLabels(stack, mouseX, mouseY);

        // Climate at the current player
        final float averageTemp = ClimateRenderCache.INSTANCE.getAverageTemperature();
        final float averageRainfall = ClimateRenderCache.INSTANCE.getAverageRainfall();
        final float rainVar = ClimateRenderCache.INSTANCE.getRainVariance();
        final float currentTemp = ClimateRenderCache.INSTANCE.getInstantTemperature();
        final float currentRainfall = ClimateRenderCache.INSTANCE.getInstantRainfall();

        final TemperatureDisplayStyle style = TFCConfig.CLIENT.climateTooltipStyle.get();

        drawLine(stack, Helpers.translateEnum(KoppenClimateClassification.classify(averageTemp, averageRainfall, rainVar, ClientHelpers.inNorthernHemisphere())), CENTER, 18);

        drawLine(stack, Component.translatable("tfc.tooltip.climate_temperature_name"), LEFT, 32);
        drawLine(stack, Component.translatable("tfc.tooltip.climate_temperature_average", style.formatRange(averageTemp)), LEFT, -1, 36, 32);
        drawLine(stack, Component.translatable("tfc.tooltip.climate_temperature_now", style.formatRange(currentTemp)), LEFT, -1, 96, 32);


        drawLine(stack, Component.translatable("tfc.tooltip.climate_rainfall_name"), LEFT, 0x202080, 46);
        drawLine(stack, Component.translatable("tfc.tooltip.climate_rainfall_average", String.format("%.0f", averageRainfall)), LEFT, 0x202080, 36, 46);
        drawLine(stack, Component.translatable("tfc.tooltip.climate_rainfall_now", String.format("%.0f", currentRainfall)), LEFT, 0x202080, 96, 46);

        drawLine(stack, Component.translatable("tfc.tooltip.climate_peak_rainfall"), LEFT, 0x202080, 57);

        drawLine(stack, Component.translatable(rainVar > 0 ? "tfc.tooltip.climate_peak_rainfall_july" : "tfc.tooltip.climate_peak_rainfall_january", String.format("%.0f", averageRainfall * (1 + Math.abs(rainVar)))), LEFT, 0x202080, 36, 57);
    }

}