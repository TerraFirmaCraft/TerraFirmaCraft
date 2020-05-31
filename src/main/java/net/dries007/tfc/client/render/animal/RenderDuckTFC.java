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

import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.client.model.animal.ModelDuckTFC;
import net.dries007.tfc.objects.entity.animal.EntityAnimalTFC;
import net.dries007.tfc.objects.entity.animal.EntityDuckTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderDuckTFC extends RenderLiving<EntityDuckTFC>
{
    private static final ResourceLocation DUCK_OLD = new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/duck_old.png");
    private static final ResourceLocation DUCK_YOUNG = new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/duck_young.png");

    private static final ResourceLocation DRAKE_OLD = new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/drake_old.png");
    private static final ResourceLocation DRAKE_YOUNG = new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/drake_young.png");

    private static final ResourceLocation DUCKLING_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/duckling.png");

    public RenderDuckTFC(RenderManager manager)
    {
        super(manager, new ModelDuckTFC(), 0.3F);
    }

    @Override
    public void doRender(EntityDuckTFC duck, double par2, double par4, double par6, float par8, float par9)
    {
        this.shadowSize = (float) (0.15f + duck.getPercentToAdulthood() * 0.15f);
        super.doRender(duck, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityDuckTFC duck)
    {
        float percent = (float) duck.getPercentToAdulthood();

        if (percent < 0.65f)
        {
            return DUCKLING_TEXTURE;
        }
        else if (duck.getGender() == EntityAnimalTFC.Gender.MALE)
        {
            return duck.getAge() == IAnimalTFC.Age.OLD ? DRAKE_OLD : DRAKE_YOUNG;
        }
        else
        {
            return duck.getAge() == IAnimalTFC.Age.OLD ? DUCK_OLD : DUCK_YOUNG;
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