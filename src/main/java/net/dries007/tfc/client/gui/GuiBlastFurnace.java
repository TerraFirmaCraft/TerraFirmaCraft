package net.dries007.tfc.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.te.TEBlastFurnace;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;
import static net.dries007.tfc.api.capability.heat.CapabilityItemHeat.MAX_TEMPERATURE;

@SideOnly(Side.CLIENT)
public class GuiBlastFurnace extends GuiContainerTE<TEBlastFurnace>
{
    private static final ResourceLocation BLAST_FURNACE_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/blast_furnace.png");

    public GuiBlastFurnace(Container container, InventoryPlayer playerInv, TEBlastFurnace tile)
    {
        super(container, playerInv, tile, BLAST_FURNACE_BACKGROUND);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        int temperature = (int) (51 * tile.getField(TEBlastFurnace.FIELD_TEMPERATURE) / MAX_TEMPERATURE);
        if (temperature > 0) {
            drawTexturedModalRect(guiLeft + 8, guiTop + 66 - temperature, 176, 0, 15, 5);
        }

        int oreCount = tile.getField(TEBlastFurnace.FIELD_ORE) * 4;
        if (oreCount > 0) {
            drawTexturedModalRect(guiLeft + 40, guiTop + 25, 176, 0, oreCount + 1, 8);
        }

        int fuelCount = tile.getField(TEBlastFurnace.FIELD_FUEL) * 4;
        if (fuelCount > 0) {
            drawTexturedModalRect(guiLeft + 40, guiTop + 43, 176, 0, fuelCount + 1, 8);
        }
    }
}
