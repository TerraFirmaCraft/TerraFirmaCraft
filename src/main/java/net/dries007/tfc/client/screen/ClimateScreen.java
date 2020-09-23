/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.screen;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screen.inventory.InventoryScreen;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.IWorld;
import net.minecraftforge.fml.network.PacketDistributor;

import net.dries007.tfc.client.screen.button.PlayerInventoryTabButton;
import net.dries007.tfc.common.container.SimpleContainer;
import net.dries007.tfc.network.PacketHandler;
import net.dries007.tfc.network.SwitchInventoryTabPacket;
import net.dries007.tfc.util.Climate;
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
        addButton(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 4, 20, 22, 128, 0, 1, 3, 0, 0, button -> {
            playerInventory.player.openContainer = playerInventory.player.container;
            Minecraft.getInstance().displayGuiScreen(new InventoryScreen(playerInventory.player));
            PacketHandler.send(PacketDistributor.SERVER.noArg(), new SwitchInventoryTabPacket(SwitchInventoryTabPacket.Type.INVENTORY));
        }));
        addButton(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 27, 20, 22, 128, 0, 1, 3, 32, 0, SwitchInventoryTabPacket.Type.CALENDAR));
        addButton(new PlayerInventoryTabButton(guiLeft, guiTop, 176, 50, 20, 22, 128, 0, 1, 3, 64, 0, SwitchInventoryTabPacket.Type.NUTRITION));
        addButton(new PlayerInventoryTabButton(guiLeft, guiTop, 176 - 3, 73, 20 + 3, 22, 128 + 20, 0, 1, 3, 96, 0, button -> {}));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        String tooltip = TextFormatting.WHITE + "" + TextFormatting.UNDERLINE + title.getFormattedText();
        font.drawString(tooltip, (xSize - font.getStringWidth(tooltip)) / 2f, 7, 0x404040);

        IWorld world = playerInventory.player.world;
        BlockPos pos = playerInventory.player.getPosition();

        float averageTemp = Climate.getAverageTemperature(world, pos);
        float rainfall = Climate.getRainfall(world, pos);
        float currentTemp = Climate.getTemperature(world, pos);

        String climateType = I18n.format("tfc.tooltip.climate_koppen_climate_classification") + I18n.format(Helpers.getEnumTranslationKey(KoppenClimateClassification.classify(averageTemp, rainfall)));
        String averageTempTooltip = I18n.format("tfc.tooltip.climate_average_temperature", String.format("%.1f", averageTemp));
        String rainfallTooltip = I18n.format("tfc.tooltip.climate_annual_rainfall", String.format("%.1f", rainfall));
        String currentTempTooltip = I18n.format("tfc.tooltip.climate_current_temp", String.format("%.1f", currentTemp));

        font.drawString(climateType, (xSize - font.getStringWidth(climateType)) / 2f, 25, 0x404040);
        font.drawString(averageTempTooltip, (xSize - font.getStringWidth(averageTempTooltip)) / 2f, 34, 0x404040);
        font.drawString(rainfallTooltip, (xSize - font.getStringWidth(rainfallTooltip)) / 2f, 43, 0x404040);
        font.drawString(currentTempTooltip, (xSize - font.getStringWidth(currentTempTooltip)) / 2f, 52, 0x404040);
    }
}
