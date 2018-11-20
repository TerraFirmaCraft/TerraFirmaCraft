/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.te.TECharcoalForge;

import static net.dries007.tfc.api.capability.heat.CapabilityItemHeat.MAX_TEMPERATURE;
import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@SideOnly(Side.CLIENT)
public class GuiCharcoalForge extends GuiContainerTFC
{

    private static final ResourceLocation CHARCOAL_FORGE_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/charcoal_forge.png");

    private TECharcoalForge te;

    public GuiCharcoalForge(Container container, InventoryPlayer playerInv, TECharcoalForge te)
    {
        super(container, playerInv, CHARCOAL_FORGE_BACKGROUND);

        this.te = te;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        // Draw the temperature indicator
        int temperature = (int) (51 * te.getField(TECharcoalForge.FIELD_TEMPERATURE) / MAX_TEMPERATURE);
        if (temperature > 0)
        {
            drawTexturedModalRect(x + 8, y + 66 - temperature, 176, 0, 15, 5);
        }
    }
}
