/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import java.io.IOException;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.client.TFCGuiHandler;
import net.dries007.tfc.client.button.GuiButtonAnvilStep;
import net.dries007.tfc.client.button.IButtonTooltip;
import net.dries007.tfc.network.PacketAnvilButton;
import net.dries007.tfc.objects.te.TEAnvilTFC;
import net.dries007.tfc.util.forge.ForgeStep;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;
import static net.dries007.tfc.objects.te.TEAnvilTFC.FIELD_PROGRESS;
import static net.dries007.tfc.objects.te.TEAnvilTFC.FIELD_TARGET;

public class GuiAnvilTFC extends GuiContainerTE<TEAnvilTFC>
{
    public static final ResourceLocation ANVIL_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/anvil.png");
    public static final int BUTTON_ID_STEP_MIN = 0;
    public static final int BUTTON_ID_STEP_MAX = 7;
    public static final int BUTTON_ID_PLAN = 8;

    public GuiAnvilTFC(Container container, InventoryPlayer playerInv, TEAnvilTFC tile)
    {
        super(container, playerInv, tile, ANVIL_BACKGROUND);
        ySize = 191;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        int buttonID = -1;
        for (ForgeStep step : ForgeStep.values())
        {
            addButton(new GuiButtonAnvilStep(++buttonID, guiLeft, guiTop, step));
        }

        addButton(new GuiButtonAnvilStep(++buttonID, guiLeft, guiTop));
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY)
    {
        // Rule tooltips

        /*for (int i = FIELD_FIRST_RULE; i <= FIELD_THIRD_RULE; i++)
        {
            if (mouseX >= x && mouseY >= y && mouseX < x + 18 && mouseY < y + 24)
            {
                ForgeRule rule = ForgeRule.valueOf(tile.getField(i));
                if (rule != null)
                {
                    drawHoveringText(I18n.format(MOD_ID + ".tooltip." + rule.name().toLowerCase()), mouseX, mouseY);
                }
            }
            x += 22;
        }*/

        // Button Tooltips
        for (GuiButton button : buttonList)
        {
            if (button instanceof IButtonTooltip && button.isMouseOver())
            {
                IButtonTooltip tooltip = (IButtonTooltip) button;
                if (tooltip.hasTooltip())
                {
                    drawHoveringText(I18n.format(tooltip.getTooltip()), mouseX, mouseY);
                }
            }
        }
        super.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        super.drawGuiContainerBackgroundLayer(partialTicks, mouseX, mouseY);

        // Draw the progress indicators
        int progress = tile.getField(FIELD_PROGRESS);
        drawTexturedModalRect(guiLeft + 13 + progress, guiTop + 100, 176, 0, 5, 5);

        int target = tile.getField(FIELD_TARGET);
        drawTexturedModalRect(guiLeft + 13 + target, guiTop + 94, 181, 0, 5, 5);

    }

    @Override
    protected void actionPerformed(GuiButton button) throws IOException
    {
        if (button.id == BUTTON_ID_PLAN)
        {
            TFCGuiHandler.openGui(tile.getWorld(), tile.getPos(), playerInv.player, TFCGuiHandler.Type.ANVIL_PLAN);
        }
        else if (button.id >= BUTTON_ID_STEP_MIN && button.id <= BUTTON_ID_STEP_MAX)
        {
            TerraFirmaCraft.getNetwork().sendToServer(new PacketAnvilButton(button.id));
        }
        super.actionPerformed(button);
    }

}
