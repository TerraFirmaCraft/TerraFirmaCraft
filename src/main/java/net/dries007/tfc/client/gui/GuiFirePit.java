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

public class GuiFirePit extends GuiContainerTE<TEFirePit>
{
    private static final ResourceLocation FIRE_PIT_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/fire_pit.png");

    public GuiFirePit(Container container, InventoryPlayer playerInv, TEFirePit tile)
    {
        super(container, playerInv, tile, FIRE_PIT_BACKGROUND);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        // Draw the fire / burn time indicator
        int temperature = (int) (51 * tile.getField(TEFirePit.FIELD_TEMPERATURE) / MAX_TEMPERATURE);
        if (temperature > 0)
        {
            drawTexturedModalRect(guiLeft + 30, guiTop + 66 - temperature, 176, 0, 15, 5);
        }
    }
}
