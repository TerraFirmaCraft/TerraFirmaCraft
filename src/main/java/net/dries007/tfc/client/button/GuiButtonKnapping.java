/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.button;

import javax.annotation.Nonnull;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.TerraFirmaCraft;
import net.dries007.tfc.network.PacketKnappingUpdate;

@SideOnly(Side.CLIENT)
public class GuiButtonKnapping extends GuiButton
{
    private final ResourceLocation texture;

    public GuiButtonKnapping(int id, int x, int y, int width, int height, ResourceLocation texture)
    {
        super(id, x, y, width, height, "");
        this.texture = texture;
    }

    public void onClick()
    {
        if (this.enabled)
        {
            this.visible = false;
            TerraFirmaCraft.getNetwork().sendToServer(new PacketKnappingUpdate(id));
        }
    }

    @Override
    public void drawButton(@Nonnull Minecraft mc, int mouseX, int mouseY, float partialTicks)
    {
        if (this.visible)
        {
            GlStateManager.color(1, 1, 1, 1);
            mc.getTextureManager().bindTexture(texture);

            hovered = mouseX >= this.x && mouseY >= this.y && mouseX < this.x + this.width && mouseY < this.y + this.height;

            //int i = this.getHoverState(this.hovered);
            //GlStateManager.enableBlend();
            //GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
            //GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);

            drawModalRectWithCustomSizedTexture(x, y, 0, 0, 16, 16, 16, 16);
            //drawTexturedModalRect(x, y, 0, 0, width, height);
            //drawTexturedModalRect(this.x, this.y, 0, 46 + i * 20, this.width / 2, this.height);
            //drawTexturedModalRect(this.x + this.width / 2, this.y, 200 - this.width / 2, 46 + i * 20, this.width / 2, this.height);
            mouseDragged(mc, mouseX, mouseY);
        }
    }
}
