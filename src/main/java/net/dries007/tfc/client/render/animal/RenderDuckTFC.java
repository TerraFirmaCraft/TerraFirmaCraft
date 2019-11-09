/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.animal.ModelDuckTFC;
import net.dries007.tfc.objects.entity.animal.EntityAnimalTFC;
import net.dries007.tfc.objects.entity.animal.EntityDuckTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderDuckTFC extends RenderLiving<EntityDuckTFC>
{
    private static final ResourceLocation DUCK_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/duck.png");
    private static final ResourceLocation DRAKE_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/drake.png");
    private static final ResourceLocation DUCKLING_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/duckling.png");

    public RenderDuckTFC(RenderManager manager)
    {
        super(manager, new ModelDuckTFC(), 0.3F);
    }

    @Override
    public void doRender(EntityDuckTFC duck, double par2, double par4, double par6, float par8, float par9)
    {
        this.shadowSize = 0.15f + duck.getPercentToAdulthood() * 0.15f;
        super.doRender(duck, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityDuckTFC duck)
    {
        float percent = duck.getPercentToAdulthood();

        if (percent < 0.65f)
        {
            return DUCKLING_TEXTURE;
        }
        else if (duck.getGender() == EntityAnimalTFC.Gender.MALE)
        {
            return DRAKE_TEXTURE;
        }
        else
        {
            return DUCK_TEXTURE;
        }
    }

    @Override
    protected float handleRotationFloat(EntityDuckTFC livingBase, float partialTicks)
    {
        float f = livingBase.oFlap + (livingBase.wingRotation - livingBase.oFlap) * partialTicks;
        float f1 = livingBase.oFlapSpeed + (livingBase.destPos - livingBase.oFlapSpeed) * partialTicks;
        return (MathHelper.sin(f) + 1.0F) * f1;
    }

    @Override
    protected void preRenderCallback(EntityDuckTFC bear, float par2)
    {
        GlStateManager.scale(0.7f, 0.7f, 0.7f);
    }
}