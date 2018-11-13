/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidStack;

import net.dries007.tfc.objects.blocks.wood.BlockBarrel;
import net.dries007.tfc.objects.te.TEBarrel;

public class TESRBarrel extends TileEntitySpecialRenderer<TEBarrel>
{
    private static final Map<Fluid, TextureAtlasSprite> FLUID_SPRITE_CACHE = new HashMap<>();

    @Override
    public void render(TEBarrel te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        if (te.getWorld().getBlockState(te.getPos()).getValue(BlockBarrel.SEALED))
        {
            return;
        }

        FluidStack fluidStack = te.tank.getFluid();

        if (fluidStack == null)
        {
            return;
        }

        Fluid fluid = fluidStack.getFluid();
        TextureAtlasSprite sprite = FLUID_SPRITE_CACHE.get(fluid);

        if (sprite == null)
        {
            sprite = Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(fluid.getStill().toString());
            FLUID_SPRITE_CACHE.put(fluid, sprite);
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        int color = fluid.getColor();

        float r = ((color >> 16) & 0xFF) / 255f;
        float g = ((color >> 8) & 0xFF) / 255f;
        float b = (color & 0xFF) / 255f;
        float a = ((color >> 24) & 0xFF) / 255f;

        GlStateManager.color(r, g, b, a);

        rendererDispatcher.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX_NORMAL);

        double height = te.fillHeightForRender;

        buffer.pos(0.1875, height, 0.1875).tex(sprite.getInterpolatedU(3), sprite.getInterpolatedV(3)).normal(0, 0, 1).endVertex();
        buffer.pos(0.1875, height, 0.8125).tex(sprite.getInterpolatedU(3), sprite.getInterpolatedV(13)).normal(0, 0, 1).endVertex();
        buffer.pos(0.8125, height, 0.8125).tex(sprite.getInterpolatedU(13), sprite.getInterpolatedV(13)).normal(0, 0, 1).endVertex();
        buffer.pos(0.8125, height, 0.1875).tex(sprite.getInterpolatedU(13), sprite.getInterpolatedV(3)).normal(0, 0, 1).endVertex();

        Tessellator.getInstance().draw();

        GlStateManager.popMatrix();
    }

    public static void clearFluidSpriteCache()
    {
        FLUID_SPRITE_CACHE.clear();
    }
}
