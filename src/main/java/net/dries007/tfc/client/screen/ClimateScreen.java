/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.inventory.InventoryScreen;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.network.PacketDistributor;

import com.mojang.blaze3d.vertex.PoseStack;
import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.container.Container;
import net.dries007.tfc.compat.patchouli.PatchouliIntegration;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.climate.KoppenClimateClassification;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class ClimateScreen extends TFCContainerScreen<Container>
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/player_climate.png");

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
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new SwitchInventoryTabPacket(SwitchInventoryTabPacket.Type.INVENTORY));
        }));
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, 176, 27, 20, 22, 128, 0, 1, 3, 32, 0, SwitchInventoryTabPacket.Type.CALENDAR));
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, 176, 50, 20, 22, 128, 0, 1, 3, 64, 0, SwitchInventoryTabPacket.Type.NUTRITION));
        addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, 176 - 3, 73, 20 + 3, 22, 128 + 20, 0, 1, 3, 96, 0, button -> {}));
        PatchouliIntegration.ifEnabled(() -> addRenderableWidget(new PlayerInventoryTabButton(leftPos, topPos, 176, 96, 20, 22, 128, 0, 1, 3, 0, 32, SwitchInventoryTabPacket.Type.BOOK)));
    }

    @Override
    protected void renderLabels(PoseStack stack, int mouseX, int mouseY)
    {
        super.renderLabels(stack, mouseX, mouseY);

        // Climate at the current player
        final float averageTemp = ClimateRenderCache.INSTANCE.getAverageTemperature();
        final float rainfall = ClimateRenderCache.INSTANCE.getRainfall();
        final float currentTemp = ClimateRenderCache.INSTANCE.getTemperature();

        drawCenteredLine(stack, Helpers.translatable("tfc.tooltip.climate_koppen_climate_classification", Helpers.translateEnum(KoppenClimateClassification.classify(averageTemp, rainfall))), 17);
        drawCenteredLine(stack, Helpers.translatable("tfc.tooltip.climate_plate_tectonics_classification", Helpers.translateEnum(ClimateRenderCache.INSTANCE.getPlateTectonicsInfo())), 28);
        drawCenteredLine(stack, Helpers.translatable("tfc.tooltip.climate_average_temperature", String.format("%.1f", averageTemp)), 39);
        drawCenteredLine(stack, Helpers.translatable("tfc.tooltip.climate_annual_rainfall", String.format("%.1f", rainfall)), 50);
        drawCenteredLine(stack, Helpers.translatable("tfc.tooltip.climate_current_temp", String.format("%.1f", currentTemp)), 61);
    }
}