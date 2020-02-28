/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.te.TEBellows;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
public class TESRBellows extends TESRBase<TEBellows>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/blocks/devices/bellows_tesr.png");

    @Override
    public void render(TEBellows te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.03125D, z + 0.5D);
        GlStateManager.rotate((te.getBlockMetadata() & 3) * 90f, 0.0F, 1.0F, 0.0F);
        GlStateManager.popMatrix();

        try
        {
            GlStateManager.pushMatrix();
            GlStateManager.color(1, 1, 1, 1);
            this.bindTexture(TEXTURE);
            GlStateManager.disableLighting();

            GlStateManager.translate(x + 0.5d, y, z + 0.5d);
            GL11.glRotatef(180.0F - 90.0F * te.getBlockMetadata(), 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5d, 0.0d, -0.5d);

            Tessellator t = Tessellator.getInstance();
            BufferBuilder b = t.getBuffer();

            // TODO: make this render less of a clusterfuck
            double tileY = 1 - te.getHeight();

            b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            drawMiddle(b, tileY);
            drawTop(b, tileY);
            t.draw();
        }
        finally
        {
            GlStateManager.popMatrix();
        }
    }

    @Override
    public boolean isGlobalRenderer(TEBellows te)
    {
        return false;
    }

    private void drawMiddle(BufferBuilder b, double y)
    {
        double[][] sides = getVerticesBySide(0.125, 0.875, y, 0.875, 0.125, 0.125, "xy");

        for (double[] v : sides)
        {
            b.pos(v[0], v[1], v[2]).tex(v[4] * 0.5, v[3] * 0.5).endVertex();
        }
    }

    private void drawTop(BufferBuilder b, double y)
    {
        double[][] sides = getVerticesBySide(0, 1, 0.125 + y, 1, 0, y, "xy");
        double[][] tops = getVerticesBySide(0, 1, 0.125 + y, 1, 0, y, "z");

        for (double[] v : sides)
        {
            b.pos(v[0], v[1], v[2]).tex(v[3] * 0.0625 + 0.5, v[4] * 0.5 + 0.5).endVertex();
        }
        for (double[] v : tops)
        {
            b.pos(v[0], v[1], v[2]).tex(v[3] * 0.5, v[4] * 0.5 + 0.5).endVertex();
        }
    }
}
