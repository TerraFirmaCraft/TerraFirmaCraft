/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render;

import org.lwjgl.opengl.GL11;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.te.TEPlacedHide;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
public class TESRPlacedHide extends TileEntitySpecialRenderer<TEPlacedHide>
{
    private static final ResourceLocation BASE_TEXTURE = new ResourceLocation(MOD_ID, "textures/items/hide/large/soaked.png");
    private static final ResourceLocation SCRAPED_TEXTURE = new ResourceLocation(MOD_ID, "textures/items/hide/large/scraped.png");

    private static final double[][] VERTICES = new double[][] {
        // x, z, u, v
        {0, 0, 0, 0}, // Top
        {0, 0.25, 0, 0.25},
        {0.25, 0.25, 0.25, 0.25},
        {0.25, 0, 0.25, 0},
    };

    @Override
    public void render(TEPlacedHide te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        short positions = te.getScrapedPositions();
        GlStateManager.pushMatrix();
        GlStateManager.translate(x, y, z);
        GlStateManager.disableLighting();

        drawTiles(BASE_TEXTURE, positions, 0);
        drawTiles(SCRAPED_TEXTURE, positions, 1);

        GlStateManager.popMatrix();
    }

    @Override
    public boolean isGlobalRenderer(TEPlacedHide te)
    {
        return false;
    }

    private void drawTiles(ResourceLocation texture, short positions, int condition)
    {
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder buffer = tessellator.getBuffer();

        bindTexture(texture);
        buffer.begin(GL11.GL_QUADS, DefaultVertexFormats.POSITION_TEX);
        for (int xOffset = 0; xOffset < 4; xOffset++)
        {
            for (int zOffset = 0; zOffset < 4; zOffset++)
            {
                // Checks the nth bit of positions
                if ((((positions >> (xOffset + 4 * zOffset)) & 1) == condition))
                {
                    for (double[] vertex : VERTICES)
                    {
                        buffer.pos(0.25 * xOffset + vertex[0], 0.01, 0.25 * zOffset + vertex[1]).tex(0.25 * xOffset + vertex[2], 0.25 * zOffset + vertex[3]).endVertex();
                    }
                }
            }
        }
        tessellator.draw();
    }
}
