/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.objects.entity.EntityJavelin;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@SideOnly(Side.CLIENT)
public class RenderJavelin extends Render<EntityJavelin>
{
    public static final ResourceLocation RES_JAVELIN = new ResourceLocation(MOD_ID, "textures/entity/projectiles/javelin.png");

    public RenderJavelin(RenderManager renderManagerIn)
    {
        super(renderManagerIn);
    }

    @Override
    public void doRender(EntityJavelin entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        this.bindEntityTexture(entity);
        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks - 90.0F, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, 0.0F, 0.0F, 1.0F);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        byte var11 = 0;
        float f2 = 0.0F;
        float f3 = 0.5F;
        float f4 = (0 + var11 * 10) / 32.0F;
        float f5 = (5 + var11 * 10) / 32.0F;
        //float f6 = 0.0F;
        //float f7 = 0.15625F;
        //float f8 = (5 + var11 * 10) / 32.0F;
        //float f9 = (10 + var11 * 10) / 32.0F;
        float f10 = 0.05625F;

        GlStateManager.enableRescaleNormal();
        float var21 = entity.arrowShake - partialTicks;

        if (var21 > 0.0F)
        {
            float var22 = -MathHelper.sin(var21 * 3.0F) * var21;
            GlStateManager.rotate(var22, 0.0F, 0.0F, 1.0F);
        }

        GlStateManager.rotate(45.0F, 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(f10, f10, f10);
        GlStateManager.translate(-4.0F, 0.0F, 0.0F);

        for (int i = 0; i < 4; ++i)
        {
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
            GlStateManager.glNormal3f(0.0F, 0.0F, f10);
            bufferbuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
            bufferbuilder.pos(-28.0D, -2.0D, 0.0D).tex(f2, f4).endVertex();
            bufferbuilder.pos(8.0D, -2.0D, 0.0D).tex(f3, f4).endVertex();
            bufferbuilder.pos(8.0D, 2.0D, 0.0D).tex(f3, f5).endVertex();
            bufferbuilder.pos(-28.0D, 2.0D, 0.0D).tex(f2, f5).endVertex();
            tessellator.draw();
        }

        GlStateManager.disableRescaleNormal();
        GlStateManager.popMatrix();
        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityJavelin entity)
    {
        return RES_JAVELIN;
    }
}