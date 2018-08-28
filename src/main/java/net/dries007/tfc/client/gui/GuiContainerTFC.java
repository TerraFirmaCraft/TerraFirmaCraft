/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.gui;

import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.resources.I18n;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.util.TFCConstants;
import net.dries007.tfc.objects.blocks.BlocksTFC;

@SideOnly(Side.CLIENT)
public class GuiContainerTFC extends GuiContainer
{
    private static final ResourceLocation BG_TEXTURE = new ResourceLocation(TFCConstants.MOD_ID, "textures/gui/log_pile.png");
    private InventoryPlayer playerInv;
    private final ResourceLocation background;
    private final String translationKey;

    protected InventoryPlayer playerInv;

    public GuiContainerTFC(Container container, InventoryPlayer playerInv, ResourceLocation background, String titleKey)
    {
        super(container);
        this.playerInv = playerInv;
        this.background = background;
        this.translationKey = titleKey;
    }

    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks)
    {
        this.drawDefaultBackground();
        super.drawScreen(mouseX, mouseY, partialTicks);
        this.renderHoveredToolTip(mouseX, mouseY);
    }

    @Override
    protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
    {
        drawSimpleForeground();
    }

    @Override
    protected void drawGuiContainerBackgroundLayer(float partialTicks, int mouseX, int mouseY)
    {
        drawSimpleBackground();
    }

    protected void drawSimpleForeground()
    {
        String name = I18n.format(translationKey + ".name");
        fontRenderer.drawString(name, xSize / 2 - fontRenderer.getStringWidth(name) / 2, 6, 0x404040);
        fontRenderer.drawString(playerInv.getDisplayName().getUnformattedText(), 8, ySize - 94, 0x404040);
    }

    protected void drawSimpleBackground()
    {
        GlStateManager.color(1, 1, 1, 1);
        mc.getTextureManager().bindTexture(background);
        int x = (width - xSize) / 2;
        int y = (height - ySize) / 2;
        drawTexturedModalRect(x, y, 0, 0, xSize, ySize);
    }
}
