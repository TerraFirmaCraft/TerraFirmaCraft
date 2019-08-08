/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.ModelThatchBed;
import net.dries007.tfc.objects.te.TEThatchBed;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@SideOnly(Side.CLIENT)
public class TESRThatchBed extends TileEntitySpecialRenderer<TEThatchBed>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/models/thatchbed.png");
    private ModelThatchBed model = new ModelThatchBed();

    public TESRThatchBed()
    {
    }

    @Override
    public void render(TEThatchBed te, double x, double y, double z, float partialTicks, int destroyStage, float alpha)
    {
        if (destroyStage >= 0)
        {
            this.bindTexture(DESTROY_STAGES[destroyStage]);
            GlStateManager.matrixMode(5890);
            GlStateManager.pushMatrix();
            GlStateManager.scale(4.0F, 4.0F, 1.0F);
            GlStateManager.translate(0.0625F, 0.0625F, 0.0625F);
            GlStateManager.matrixMode(5888);
        }
        else
        {
            this.bindTexture(TEXTURE);
        }

        if (te.hasWorld())
        {
            this.renderPiece(te.isHeadPiece(), x, y, z, te.getBlockFacing(), alpha);
        }

        if (destroyStage >= 0)
        {
            GlStateManager.matrixMode(5890);
            GlStateManager.popMatrix();
            GlStateManager.matrixMode(5888);
        }
    }

    private void renderPiece(boolean isHeadPiece, double x, double y, double z, EnumFacing facing, float alpha)
    {
        this.model.preparePiece(isHeadPiece);
        GlStateManager.pushMatrix();
        float f = 0.0F;
        float f1 = 0.0F;
        float f2 = 0.0F;

        if (facing == EnumFacing.NORTH)
        {
            f = 0.0F;
        }
        else if (facing == EnumFacing.SOUTH)
        {
            f = 180.0F;
            f1 = 1.0F;
            f2 = 1.0F;
        }
        else if (facing == EnumFacing.WEST)
        {
            f = -90.0F;
            f2 = 1.0F;
        }
        else if (facing == EnumFacing.EAST)
        {
            f = 90.0F;
            f1 = 1.0F;
        }

        GlStateManager.translate((float) x + f1, (float) y + 0.5625F, (float) z + f2);
        GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.rotate(f, 0.0F, 0.0F, 1.0F);
        GlStateManager.enableRescaleNormal();
        GlStateManager.pushMatrix();
        this.model.render();
        GlStateManager.popMatrix();
        GlStateManager.color(1.0F, 1.0F, 1.0F, alpha);
        GlStateManager.popMatrix();
    }
}