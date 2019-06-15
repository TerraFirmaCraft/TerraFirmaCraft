/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.nutrient.CapabilityFood;
import net.dries007.tfc.api.capability.nutrient.IPlayerNutrients;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.client.button.GuiButtonPlayerInventoryTab;
import net.dries007.tfc.network.PacketSwitchPlayerInventoryTab;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.agriculture.Nutrient;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@SideOnly(Side.CLIENT)
public class GuiNutrition extends GuiContainerTFC
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/player_nutrition.png");

    private float[] cachedNutrients;

    public GuiNutrition(Container container, InventoryPlayer playerInv)
    {
        super(container, playerInv, BACKGROUND);

        cachedNutrients = new float[Nutrient.TOTAL];
        IPlayerNutrients cap = playerInv.player.getCapability(CapabilityFood.CAPABILITY_PLAYER_NUTRIENTS, null);
        if (cap != null)
        {
            for (Nutrient n : Nutrient.values())
                cachedNutrients[n.ordinal()] = cap.getNutrient(n);
        }
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int buttonId = 0;
        addButton(new GuiButtonPlayerInventoryTab(TFCGuiHandler.Type.INVENTORY, guiLeft, guiTop, ++buttonId, true));
        addButton(new GuiButtonPlayerInventoryTab(TFCGuiHandler.Type.SKILLS, guiLeft, guiTop, ++buttonId, true));
        addButton(new GuiButtonPlayerInventoryTab(TFCGuiHandler.Type.CALENDAR, guiLeft, guiTop, ++buttonId, true));
        addButton(new GuiButtonPlayerInventoryTab(TFCGuiHandler.Type.NUTRITION, guiLeft, guiTop, ++buttonId, false));
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        super.drawGuiContainerForegroundLayer(mouseX, mouseY);

        for (Nutrient n : Nutrient.values())
        {
            String stuff = I18n.format(Helpers.getEnumName(n)) + ": " + cachedNutrients[n.ordinal()];
            fontRenderer.drawString(stuff, xSize / 2 - fontRenderer.getStringWidth(stuff) / 2, 10 + 12 * n.ordinal(), 0x404040);
        }
    }

    @Override
    protected void actionPerformed(GuiButton button)
    {
        if (button instanceof GuiButtonPlayerInventoryTab && ((GuiButtonPlayerInventoryTab) button).isActive())
        {
            GuiButtonPlayerInventoryTab tabButton = (GuiButtonPlayerInventoryTab) button;
            if (tabButton.isActive())
            {
                if (tabButton.getGuiType() == TFCGuiHandler.Type.INVENTORY)
                {
                    this.mc.displayGuiScreen(new GuiInventory(playerInv.player));
                }
                TerraFirmaCraft.getNetwork().sendToServer(new PacketSwitchPlayerInventoryTab(tabButton.getGuiType()));
            }
        }
    }
}
