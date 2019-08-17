/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.animal.ModelWolfTFC;
import net.dries007.tfc.objects.entity.animal.EntityWolfTFC;

@SideOnly(Side.CLIENT)
public class RenderWolfTFC extends RenderLiving<EntityWolfTFC>
{
    private static final ResourceLocation WOLF_TEXTURES = new ResourceLocation("textures/entity/wolf/wolf.png");
    private static final ResourceLocation TAMED_WOLF_TEXTURES = new ResourceLocation("textures/entity/wolf/wolf_tame.png");
    private static final ResourceLocation ANRGY_WOLF_TEXTURES = new ResourceLocation("textures/entity/wolf/wolf_angry.png");

    public RenderWolfTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelWolfTFC(), 0.5F);
        this.addLayer(new LayerWolfCollarTFC(this));
    }

    /**
     * Renders the desired {@code T} type Entity.
     */
    public void doRender(EntityWolfTFC entity, double x, double y, double z, float entityYaw, float partialTicks)
    {
        if (entity.isWolfWet())
        {
            float f = entity.getBrightness() * entity.getShadingWhileWet(partialTicks);
            GlStateManager.color(f, f, f);
        }

        super.doRender(entity, x, y, z, entityYaw, partialTicks);
    }

    /**
     * Defines what float the third param in setRotationAngles of ModelBase is
     */
    protected float handleRotationFloat(EntityWolfTFC livingBase, float partialTicks)
    {
        return livingBase.getTailRotation();
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    protected ResourceLocation getEntityTexture(EntityWolfTFC entity)
    {
        if (entity.isTamed())
        {
            return TAMED_WOLF_TEXTURES;
        }
        else
        {
            return entity.isAngry() ? ANRGY_WOLF_TEXTURES : WOLF_TEXTURES;
        }
    }
}
