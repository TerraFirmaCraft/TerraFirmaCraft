/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.client.model.animal.ModelBearTFC;
import net.dries007.tfc.objects.entity.animal.EntityBearTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class RenderBearTFC extends RenderLiving<EntityBearTFC>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/bear.png");

    public RenderBearTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelBearTFC(), 0.7F);
    }

    @Override
    public void doRender(@Nonnull EntityBearTFC bear, double par2, double par4, double par6, float par8, float par9)
    {
        this.shadowSize = 0.35f + (bear.getPercentToAdulthood() * 0.35f);
        super.doRender(bear, par2, par4, par6, par8, par9);
    }

    @Override
    protected float handleRotationFloat(EntityBearTFC par1EntityLiving, float par2)
    {
        return 1.0f;
    }

    @Override
    protected void preRenderCallback(EntityBearTFC bear, float par2)
    {
        GlStateManager.scale(1.3f, 1.3f, 1.3f);
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityBearTFC entity)
    {
        return TEXTURE;
    }
}