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

import net.dries007.tfc.client.model.animal.ModelPigTFC;
import net.dries007.tfc.objects.entity.animal.EntityPigTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class RenderPigTFC extends RenderLiving<EntityPigTFC>
{
    private static final ResourceLocation PIG_TEXTURES = new ResourceLocation(MOD_ID, "textures/entity/animal/pig.png");

    public RenderPigTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelPigTFC(), 0.7F);
    }

    @Override
    public void doRender(EntityPigTFC pig, double par2, double par4, double par6, float par8, float par9)
    {
        this.shadowSize = 0.35f + pig.getPercentToAdulthood() * 0.35f;
        super.doRender(pig, par2, par4, par6, par8, par9);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityPigTFC entity)
    {
        return PIG_TEXTURES;
    }

    @Override
    protected void preRenderCallback(EntityPigTFC par1EntityLivingBase, float par2)
    {
        float scale = 1 / 2f + 0.5f;
        GlStateManager.scale(scale, scale, scale);
    }
}