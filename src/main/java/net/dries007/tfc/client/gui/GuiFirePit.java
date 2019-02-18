/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.objects.te.TEFirePit;

import static net.dries007.tfc.api.capability.heat.CapabilityItemHeat.MAX_TEMPERATURE;
import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class GuiFirePit extends GuiContainerTFC
{
    private static final ResourceLocation FIRE_PIT_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/fire_pit.png");

    private TEFirePit te;

    public GuiFirePit(Container container, InventoryPlayer playerInv, TEFirePit te)
    {
        super(container, playerInv, FIRE_PIT_BACKGROUND);

        this.te = te;
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;

        // Draw the fire / burn time indicator
        int temperature = (int) (51 * te.getField(TEFirePit.FIELD_TEMPERATURE) / MAX_TEMPERATURE);
        if (temperature > 0)
        {
            drawTexturedModalRect(x + 30, y + 16 - temperature, 176, 0, 15, 5);
        }
    }
}
