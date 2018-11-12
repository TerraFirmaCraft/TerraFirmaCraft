package net.dries007.tfc.client.render;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.objects.te.TEBarrel;

public class TESRBarrel extends TileEntitySpecialRenderer<TEBarrel>
{
    @Override
    public void render(TEBarrel te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        FluidStack fluid = te.tank.getFluid();

        if (fluid == null)
        {
            return;
        }

        TextureAtlasSprite texture = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getFluid().getStill().toString());

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        int color = fluid.getFluid().getColor();

        float r = ((color >> 16) & 0xFF) / 255f; // red
        float g = ((color >> 8) & 0xFF) / 255f; // green
        float b = (color & 0xFF) / 255f; // blue
        float a = ((color >> 24) & 0xFF) / 255f; // alpha

        GlStateManager.color(r, g, b, a);

        //rendererDispatcher.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

        double height = te.fillHeightForRender;

        buffer.pos(0.1875, height, 0.1875).tex(texture.getInterpolatedU(3), texture.getInterpolatedV(3)).normal(0, 0, 1).endVertex();
        buffer.pos(0.1875, height, 0.8125).tex(texture.getInterpolatedU(3), texture.getInterpolatedV(13)).normal(0, 0, 1).endVertex();
        buffer.pos(0.8125, height, 0.8125).tex(texture.getInterpolatedU(13), texture.getInterpolatedV(13)).normal(0, 0, 1).endVertex();
        buffer.pos(0.8125, height, 0.1875).tex(texture.getInterpolatedU(13), texture.getInterpolatedV(3)).normal(0, 0, 1).endVertex();

        Tessellator.getInstance().draw();

        GlStateManager.popMatrix();
    }
}
