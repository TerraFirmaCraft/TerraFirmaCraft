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

import net.dries007.tfc.client.model.animal.ModelDeerTFC;
import net.dries007.tfc.objects.entity.animal.EntityDeerTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class RenderDeerTFC extends RenderLiving<EntityDeerTFC>
{
    private static final ResourceLocation DEER_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/deer.png");
    private static final ResourceLocation FAWN_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/deer_fawn.png");

    public RenderDeerTFC(RenderManager manager)
    {
        super(manager, new ModelDeerTFC(), 0.7F);
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityDeerTFC deer)
    {
        if (deer.isChild())
        {
            return FAWN_TEXTURE;
        }
        else
        {
            return DEER_TEXTURE;
        }
    }

    @Override
    protected float handleRotationFloat(EntityDeerTFC deer, float par2)
    {
        return 1.0f;
    }

    @Override
    protected void preRenderCallback(EntityDeerTFC deer, float par2)
    {
        GlStateManager.scale(1 - 0.3f, 1 - 0.3f, 1 - 0.3f);
    }
}