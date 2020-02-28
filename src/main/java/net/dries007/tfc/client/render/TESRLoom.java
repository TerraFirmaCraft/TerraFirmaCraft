/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render;

import java.util.HashMap;
import java.util.Map;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.registries.TFCRegistries;
import net.dries007.tfc.api.types.Tree;
import net.dries007.tfc.objects.te.TELoom;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
public class TESRLoom extends TESRBase<TELoom>
{
    private static final Map<Tree, ResourceLocation> PLANKS_TEXTURES = new HashMap<>();

    static
    {
        for (Tree wood : TFCRegistries.TREES.getValuesCollection())
        {
            //noinspection ConstantConditions
            PLANKS_TEXTURES.put(wood, new ResourceLocation(MOD_ID, "textures/blocks/wood/planks/" + wood.getRegistryName().getPath() + ".png"));
        }
    }

    @Override
    public void render(TELoom te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        GlStateManager.pushMatrix();
        GlStateManager.translate(x + 0.5D, y + 0.03125D, z + 0.5D);
        GlStateManager.rotate((te.getBlockMetadata() & 3) * 90f, 0.0F, 1.0F, 0.0F);
        GlStateManager.popMatrix();

        double tileZ = te.getAnimPos();

        try
        {
            GlStateManager.pushMatrix();
            GlStateManager.color(1, 1, 1, 1);
            this.bindTexture(PLANKS_TEXTURES.get(te.getWood()));

            GlStateManager.disableLighting();

            GlStateManager.translate(x + 0.5d, y, z + 0.5d);
            GL11.glRotatef(180.0F - 90.0F * te.getBlockMetadata(), 0.0F, 1.0F, 0.0F);
            GlStateManager.translate(-0.5d, 0.0d, -0.5d);

            Tessellator t = Tessellator.getInstance();
            BufferBuilder b = t.getBuffer();

            b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
            if ("u".equals(te.getAnimElement()))
            {
                drawUpper(b, tileZ);
                drawLower(b, 0);
            }
            else
            {
                drawUpper(b, 0);
                drawLower(b, tileZ);
            }

            t.draw();
        }
        finally
        {
            GlStateManager.popMatrix();
        }

        if (te.hasRecipe())
        {
            try
            {
                GlStateManager.pushMatrix();
                GlStateManager.color(1, 1, 1, 1);
                this.bindTexture(te.getInProgressTexture());

                GlStateManager.disableLighting();

                GlStateManager.translate(x + 0.5d, y, z + 0.5d);
                GL11.glRotatef(180.0F - 90.0F * te.getBlockMetadata(), 0.0F, 1.0F, 0.0F);
                GlStateManager.translate(-0.5d, 0.0d, -0.5d);

                Tessellator t = Tessellator.getInstance();
                BufferBuilder b = t.getBuffer();

                b.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);

                double Z = tileZ * 2 / 3;

                drawMaterial(b, te.getMaxInputCount(), te.getCount(), te.getMaxProgress(), te.getProgress(), ("u".equals(te.getAnimElement())) ? Z : 0, ("l".equals(te.getAnimElement())) ? Z : 0);
                drawProduct(b, te.getMaxProgress(), te.getProgress());

                t.draw();
            }
            finally
            {
                GlStateManager.popMatrix();
            }
        }
    }

    @Override
    public boolean isGlobalRenderer(TELoom te)
    {
        return false;
    }

    private void drawProduct(BufferBuilder b, int maxProgress, int progress)
    {
        double[][] sidesZ = getPlaneVertices(0.1875, 0.9375, 0.75 - 0.001, 0.8125, 0.9375 - (0.625 / maxProgress) * progress, 0.75 - 0.001, 0, 0, 1, (double) progress / (double) 16);

        for (double[] v : sidesZ)
        {
            b.pos(v[0], v[1], v[2]).tex(v[3], v[4]).endVertex();
        }
    }

    private void drawMaterial(BufferBuilder b, int maxPieces, int pieces, int maxProgress, int progress, double Z1, double Z2)
    {
        double y1 = 0.9375 - (0.625 / maxProgress) * progress, y2, z1 = 0.75, z2;
        double texX1, texX2, texY1, texY2;
        for (int i = 0; i < pieces; i++)
        {

            if (i % 2 == 0)
            {
                texX1 = 0;
                texY1 = 0;
                texX2 = 0.0625;
                texY2 = 0.125;
                z2 = 0.75 - Z1;
                y2 = 0.34375;
            }
            else
            {
                texX1 = 0.125;
                texY1 = 0;
                texX2 = 0.1875;
                texY2 = 0.1875;
                z2 = 0.75 - Z2;
                y2 = 0.125;
            }

            double[][] sidesZ = getPlaneVertices(0.1875 + (0.625 / maxPieces) * i, y1, z1 - 0.001, 0.1875 + (0.625 / maxPieces) * (i + 1), y2, z2 - 0.001, texX1, texY1, texX2, texY2);

            for (double[] v : sidesZ)
            {
                b.pos(v[0], v[1], v[2]).tex(v[3], v[4]).endVertex();
            }

            if (i % 2 == 0)
            {
                texX1 = 0;
                texY1 = 0.5;
                texX2 = 0.0625;
                texY2 = 0.5625;
            }
            else
            {
                texX1 = 0.125;
                texY1 = 0.5;
                texX2 = 0.1875;
                texY2 = 0.5625;
            }

            sidesZ = getPlaneVertices(0.1875 + (0.625 / maxPieces) * i, 0, z1 - 0.001, 0.1875 + (0.625 / maxPieces) * (i + 1), y2, z2 - 0.001, texX1, texY1, texX2, texY2);

            for (double[] v : sidesZ)
            {
                b.pos(v[0], v[1], v[2]).tex(v[3], v[4]).endVertex();
            }
        }
    }

    private double[][] getPlaneVertices(double X1, double Y1, double Z1, double X2, double Y2, double Z2, double texX1, double texY1, double texX2, double texY2)
    {
        return new double[][] {
            {X1, Y1, Z1, texX1, texY1},
            {X2, Y1, Z1, texX2, texY1},
            {X2, Y2, Z2, texX2, texY2},
            {X1, Y2, Z2, texX1, texY2},

            {X2, Y1, Z1, texX2, texY1},
            {X1, Y1, Z1, texX1, texY1},
            {X1, Y2, Z2, texX1, texY2},
            {X2, Y2, Z2, texX2, texY2}
        };
    }

    private void drawUpper(BufferBuilder b, double z)
    {
        double[][] sidesX = getVerticesBySide(0.0625, 0.3125, 0.5626 - z, 0.9375, 0.375, 0.625 - z, "x");

        for (double[] v : sidesX)
        {
            b.pos(v[0], v[1], v[2]).tex(v[3] * 0.0625, v[4] * 0.0625).endVertex();
        }

        double[][] sidesY = getVerticesBySide(0.0625, 0.3125, 0.5626 - z, 0.9375, 0.375, 0.625 - z, "y");

        for (double[] v : sidesY)
        {
            b.pos(v[0], v[1], v[2]).tex(v[3] * 0.0625, v[4] * 0.875).endVertex();
        }

        double[][] sidesZ = getVerticesBySide(0.0625, 0.3125, 0.5626 - z, 0.9375, 0.375, 0.625 - z, "z");

        for (double[] v : sidesZ)
        {
            b.pos(v[0], v[1], v[2]).tex(v[3] * 0.875, v[4] * 0.0625).endVertex();
        }
    }

    private void drawLower(BufferBuilder b, double z)
    {
        double[][] sidesX = getVerticesBySide(0.0625, 0.09375, 0.5626 - z, 0.9375, 0.15625, 0.625 - z, "x");

        for (double[] v : sidesX)
        {
            b.pos(v[0], v[1], v[2]).tex(v[3] * 0.0625, v[4] * 0.0625).endVertex();
        }

        double[][] sidesY = getVerticesBySide(0.0625, 0.09375, 0.5626 - z, 0.9375, 0.15625, 0.625 - z, "y");

        for (double[] v : sidesY)
        {
            b.pos(v[0], v[1], v[2]).tex(v[3] * 0.0625, v[4] * 0.875).endVertex();
        }

        double[][] sidesZ = getVerticesBySide(0.0625, 0.09375, 0.5626 - z, 0.9375, 0.15625, 0.625 - z, "z");

        for (double[] v : sidesZ)
        {
            b.pos(v[0], v[1], v[2]).tex(v[3] * 0.875, v[4] * 0.0625).endVertex();
        }
    }
}
