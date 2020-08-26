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
import net.dries007.tfc.client.model.animal.ModelQuailTFC;
import net.dries007.tfc.objects.entity.animal.EntityAnimalTFC;
import net.dries007.tfc.objects.entity.animal.EntityQuailTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderQuailTFC extends RenderLiving<EntityQuailTFC>
{
    private static final ResourceLocation FEMALE_YOUNG = new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/quailf_young.png");
    private static final ResourceLocation FEMALE_OLD = new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/quailf_old.png");

    private static final ResourceLocation MALE_YOUNG = new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/quailm_young.png");
    private static final ResourceLocation MALE_OLD = new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/quailm_old.png");

    private static final ResourceLocation CHICK_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/quail_chick.png");

    public RenderQuailTFC(RenderManager manager)
    {
        super(manager, new ModelQuailTFC(), 0.3F);
    }

    @Override
    public void doRender(EntityQuailTFC quail, double par2, double par4, double par6, float par8, float par9)
    {
        this.shadowSize = (float) (0.15f + quail.getPercentToAdulthood() * 0.15f);
        super.doRender(quail, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityQuailTFC quail)
    {
        float percent = (float) quail.getPercentToAdulthood();

        if (percent < 0.65f)
        {
            return CHICK_TEXTURE;
        }
        else if (quail.getGender() == EntityAnimalTFC.Gender.MALE)
        {
            return quail.getAge() == IAnimalTFC.Age.OLD ? MALE_OLD : MALE_YOUNG;
        }
        else
        {
            return quail.getAge() == IAnimalTFC.Age.OLD ? FEMALE_OLD : FEMALE_YOUNG;
        }
    }

    @Override
    protected float handleRotationFloat(EntityQuailTFC livingBase, float partialTicks)
    {
        float f = livingBase.oFlap + (livingBase.wingRotation - livingBase.oFlap) * partialTicks;
        float f1 = livingBase.oFlapSpeed + (livingBase.destPos - livingBase.oFlapSpeed) * partialTicks;
        return (MathHelper.sin(f) + 1.0F) * f1;
    }

    @Override
    protected void preRenderCallback(EntityQuailTFC quail, float par2)
    {
        GlStateManager.scale(0.8f, 0.8f, 0.8f);
    }
}