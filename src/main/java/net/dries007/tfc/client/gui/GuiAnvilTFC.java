/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.client.button.GuiButtonAnvil;
import net.dries007.tfc.objects.te.TEAnvilTFC;
import net.dries007.tfc.util.forge.ForgeStep;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;
import static net.dries007.tfc.objects.te.TEAnvilTFC.FIELD_PROGRESS;
import static net.dries007.tfc.objects.te.TEAnvilTFC.FIELD_TARGET;

public class GuiAnvilTFC extends GuiContainerTFC
{
    public static final ResourceLocation ANVIL_BACKGROUND = new ResourceLocation(MOD_ID, "textures/gui/anvil.png");

    private final TEAnvilTFC tile;

    public GuiAnvilTFC(Container container, InventoryPlayer playerInv, TEAnvilTFC tile)
    {
        super(container, playerInv, ANVIL_BACKGROUND);

        this.tile = tile;

        ySize = 191;
        // todo: everything
    }

    @Override
    public void initGui()
    {
        super.initGui();

        // Progress buttons (left + right)
        int buttonID = 0;
        for (ForgeStep step : ForgeStep.values())
        {
            addButton(new GuiButtonAnvil(buttonID++, guiLeft, guiTop, step));
        }
    }

    @Override
    protected void renderHoveredToolTip(int mouseX, int mouseY)
    {
        // Rule tooltips
        int x = (width - xSize) / 2 + 57;
        int y = (height - ySize) / 2 + 42;

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

        // Step Button Tooltips
        for (GuiButton button : buttonList)
        {
            if (button instanceof GuiButtonAnvil)
            {
                GuiButtonAnvil buttonAnvil = (GuiButtonAnvil) button;
                if (buttonAnvil.hasTooltip() && buttonAnvil.isMouseOver())
                {
                    drawHoveringText(I18n.format(buttonAnvil.getTooltip()), mouseX, mouseY);
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
}
