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
import net.dries007.tfc.client.ClimateRenderCache;
import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.container.SimpleContainer;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.KoppenClimateClassification;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class ClimateScreen extends TFCContainerScreen<SimpleContainer>
{
    public static final ResourceLocation BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/player_climate.png");

    public ClimateScreen(SimpleContainer container, PlayerInventory playerInv, ITextComponent name)
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
        addButton(new PlayerInventoryTabButton(leftPos, topPos, 176, 27, 20, 22, 128, 0, 1, 3, 32, 0, SwitchInventoryTabPacket.Type.CALENDAR));
        addButton(new PlayerInventoryTabButton(leftPos, topPos, 176, 50, 20, 22, 128, 0, 1, 3, 64, 0, SwitchInventoryTabPacket.Type.NUTRITION));
        addButton(new PlayerInventoryTabButton(leftPos, topPos, 176 - 3, 73, 20 + 3, 22, 128 + 20, 0, 1, 3, 96, 0, button -> {}));
    }

    @Override
    protected void renderLabels(MatrixStack matrixStack, int mouseX, int mouseY)
    {
        super.renderLabels(matrixStack, mouseX, mouseY);

        // Climate at the current player
        float averageTemp = ClimateRenderCache.INSTANCE.getAverageTemperature();
        float rainfall = ClimateRenderCache.INSTANCE.getRainfall();
        float currentTemp = ClimateRenderCache.INSTANCE.getTemperature();

        String climateType = I18n.get("tfc.tooltip.climate_koppen_climate_classification") + I18n.get(Helpers.getEnumTranslationKey(KoppenClimateClassification.classify(averageTemp, rainfall)));
        String plateTectonics = I18n.get("tfc.tooltip.climate_plate_tectonics_classification") + I18n.get(Helpers.getEnumTranslationKey(ClimateRenderCache.INSTANCE.getPlateTectonicsInfo()));
        String averageTempTooltip = I18n.get("tfc.tooltip.climate_average_temperature", String.format("%.1f", averageTemp));
        String rainfallTooltip = I18n.get("tfc.tooltip.climate_annual_rainfall", String.format("%.1f", rainfall));
        String currentTempTooltip = I18n.get("tfc.tooltip.climate_current_temp", String.format("%.1f", currentTemp));

        font.draw(matrixStack, climateType, (imageWidth - font.width(climateType)) / 2f, 25, 0x404040);
        font.draw(matrixStack, plateTectonics, (imageWidth - font.width(plateTectonics)) / 2f, 34, 0x404040);
        font.draw(matrixStack, averageTempTooltip, (imageWidth - font.width(averageTempTooltip)) / 2f, 43, 0x404040);
        font.draw(matrixStack, rainfallTooltip, (imageWidth - font.width(rainfallTooltip)) / 2f, 52, 0x404040);
        font.draw(matrixStack, currentTempTooltip, (imageWidth - font.width(currentTempTooltip)) / 2f, 61, 0x404040);
    }
}