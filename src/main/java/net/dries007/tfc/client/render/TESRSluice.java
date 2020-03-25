/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render;

import java.util.Random;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.FluidSpriteCache;
import net.dries007.tfc.objects.te.TESluice;

@SideOnly(Side.CLIENT)
public class TESRSluice extends TileEntitySpecialRenderer<TESluice>
{
    private static final ItemStack GRAVEL = new ItemStack(Blocks.GRAVEL);
    private static final Random SOIL_NOISE = new Random();

    @Override
    public void render(TESluice te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        Fluid flowing = te.getFlowingFluid();
        if (flowing == null) return;

        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);

        EnumFacing facing = te.getBlockFacing();
        //noinspection ConstantConditions
        switch (facing)
        {
            case WEST:
                GlStateManager.translate(0, 0, 1);
                GlStateManager.rotate(90F, 0, 1, 0);
                break;
            case SOUTH:
                GlStateManager.translate(1, 0, 1);
                GlStateManager.rotate(180F, 0, 1, 0);
                break;
            case EAST:
                GlStateManager.translate(1, 0, 0);
                GlStateManager.rotate(270F, 0, 1, 0);
                break;
            default:
        }

        // Render soil (gravel "blocklings")
        int soilBlocks = Math.min((int) Math.ceil(te.getSoil() * 5D / TESluice.MAX_SOIL), 5);
        for (int step = 0; step < 8; step++)
        {
            double posX = 0.5D;
            double posY = 0.96875D - 0.0125D - (0.125D * step);
            double posZ = 0.15625D - 0.0125D + (0.25D * step);
            for (int soiling = 1; soiling < soilBlocks; soiling++)
            {
                // Filling from the middle outward borders
                // Also, random is reset every time to keep then from changing every time the number of soil changes
                SOIL_NOISE.setSeed(te.getPos().toLong() + soiling * 2 + step * 3);
                drawSoil(posX - (0.1D * soiling), posY, posZ, SOIL_NOISE.nextFloat() * 360F);
                drawSoil(posX + (0.1D * soiling), posY, posZ, SOIL_NOISE.nextFloat() * 360F);
            }
            if (te.getSoil() > 0)
            {
                // Always draw the middle one if there is soil
                SOIL_NOISE.setSeed(te.getPos().toLong() - 565 + step * 4);
                drawSoil(posX, posY, posZ, SOIL_NOISE.nextFloat() * 360F);
            }
        }

        rendererDispatcher.renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);

        TextureAtlasSprite sprite = FluidSpriteCache.getFlowingSprite(flowing);

        GlStateManager.enableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);

        int color = flowing.getColor();

        float r = ((color >> 16) & 0xFF) / 255F;
        float g = ((color >> 8) & 0xFF) / 255F;
        float b = (color & 0xFF) / 255F;
        float a = ((color >> 24) & 0xFF) / 255F;

        GlStateManager.color(r, g, b, a);

        BufferBuilder buffer = Tessellator.getInstance().getBuffer();

        //Top
        buffer.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX_NORMAL);

        buffer.pos(0.05D, 1.033D, 0).tex(sprite.getMinU(), sprite.getMinV()).normal(0, 0, 1).endVertex();
        buffer.pos(0.05D, -0.15D, 2.45D).tex(sprite.getMinU(), sprite.getMaxV()).normal(0, 0, 1).endVertex();
        buffer.pos(0.95D, -0.15D, 2.45D).tex(sprite.getMaxU(), sprite.getMaxV()).normal(0, 0, 1).endVertex();
        buffer.pos(0.95D, 1.033D, 0).tex(sprite.getMaxU(), sprite.getMinV()).normal(0, 0, 1).endVertex();

        Tessellator.getInstance().draw();

        //Bottom
        buffer.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX_NORMAL);

        buffer.pos(0.05D, 0.833D, 0).tex(sprite.getMinU(), sprite.getMinV()).normal(0, 0, 1).endVertex();
        buffer.pos(0.05D, -0.3D, 2.45D).tex(sprite.getMinU(), sprite.getMaxV()).normal(0, 0, 1).endVertex();
        buffer.pos(0.95D, -0.3D, 2.45D).tex(sprite.getMaxU(), sprite.getMaxV()).normal(0, 0, 1).endVertex();
        buffer.pos(0.95D, 0.833D, 0).tex(sprite.getMaxU(), sprite.getMinV()).normal(0, 0, 1).endVertex();

        Tessellator.getInstance().draw();

        //Left
        buffer.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX_NORMAL);

        buffer.pos(0.05D, -0.15D, 2.45D).tex(sprite.getMinU(), sprite.getMaxV()).normal(0, 0, 1).endVertex();
        buffer.pos(0.05D, 1.033D, 0).tex(sprite.getMinU(), sprite.getMinV()).normal(0, 0, 1).endVertex();
        buffer.pos(0.05D, 0.833D, 0).tex(sprite.getMaxU(), sprite.getMinV()).normal(0, 0, 1).endVertex();
        buffer.pos(0.05D, -0.3D, 2.45D).tex(sprite.getMaxU(), sprite.getMaxV()).normal(0, 0, 1).endVertex();

        Tessellator.getInstance().draw();

        //Right
        buffer.begin(GL11.GL_POLYGON, DefaultVertexFormats.POSITION_TEX_NORMAL);

        buffer.pos(0.95D, 1.033D, 0).tex(sprite.getMinU(), sprite.getMinV()).normal(0, 0, 1).endVertex();
        buffer.pos(0.95D, -0.15D, 2.45D).tex(sprite.getMinU(), sprite.getMaxV()).normal(0, 0, 1).endVertex();
        buffer.pos(0.95D, -0.3D, 2.45D).tex(sprite.getMaxU(), sprite.getMaxV()).normal(0, 0, 1).endVertex();
        buffer.pos(0.95D, 0.833D, 0).tex(sprite.getMaxU(), sprite.getMinV()).normal(0, 0, 1).endVertex();

        Tessellator.getInstance().draw();

        GlStateManager.popMatrix();
    }

    @Override
    public boolean isGlobalRenderer(TESluice te)
    {
        return true;
    }

    private void drawSoil(double posX, double posY, double posZ, float rotation)
    {
        GlStateManager.pushMatrix();

        GlStateManager.translate(posX, posY, posZ);
        GlStateManager.rotate(rotation, 0, 1, 0);
        GlStateManager.scale(0.1D, 0.1D, 0.1D);

        Minecraft.getMinecraft().getRenderItem().renderItem(GRAVEL, ItemCameraTransforms.TransformType.FIXED);

        GlStateManager.popMatrix();
    }
}
