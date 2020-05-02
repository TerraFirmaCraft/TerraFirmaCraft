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
import net.minecraft.util.FoodStats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.api.capability.food.IFoodStatsTFC;
import net.dries007.tfc.api.capability.food.Nutrient;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.client.button.GuiButtonPlayerInventoryTab;
import net.dries007.tfc.network.PacketSwitchPlayerInventoryTab;
import net.dries007.tfc.util.Helpers;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
public class GuiNutrition extends GuiContainerTFC
{
    private static final ResourceLocation BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/player_nutrition.png");

    private final float[] cachedNutrients;

    public GuiNutrition(Container container, InventoryPlayer playerInv)
    {
        super(container, playerInv, BACKGROUND);

        cachedNutrients = new float[Nutrient.TOTAL];

        FoodStats foodStats = playerInv.player.getFoodStats();
        if (foodStats instanceof IFoodStatsTFC)
        {
            for (Nutrient n : Nutrient.values())
            {
                cachedNutrients[n.ordinal()] = ((IFoodStatsTFC) foodStats).getNutrition().getNutrient(n);
            }
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
            String caption = I18n.format(Helpers.getEnumName(n));
            fontRenderer.drawString(caption, 112 - fontRenderer.getStringWidth(caption), 19 + 13 * n.ordinal(), 0x404040);
        }
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);
        for (Nutrient n : Nutrient.values())
        {
            int scaledNutrient = (int) (cachedNutrients[n.ordinal()] * 50);
            drawTexturedModalRect(guiLeft + 118, guiTop + 21 + 13 * n.ordinal(), 176, 0, scaledNutrient, 5);
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
