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

import net.dries007.tfc.client.model.animal.ModelPheasantTFC;
import net.dries007.tfc.objects.entity.animal.EntityAnimalTFC;
import net.dries007.tfc.objects.entity.animal.EntityPheasantTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderPheasantTFC extends RenderLiving<EntityPheasantTFC>
{
    private static final ResourceLocation CHICK_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/huntable/pheasant_chick.png");
    private static final ResourceLocation MALE_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/huntable/pheasant_male.png");
    private static final ResourceLocation FEMALE_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/huntable/pheasant_female.png");


    public RenderPheasantTFC(RenderManager manager)
    {
        super(manager, new ModelPheasantTFC(), 0.3F);
    }

    @Override
    public void doRender(EntityPheasantTFC pheasent, double par2, double par4, double par6, float par8, float par9)
    {
        this.shadowSize = (float) (0.15f + pheasent.getPercentToAdulthood() * 0.15f);
        super.doRender(pheasent, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityPheasantTFC pheasent)
    {
        float percent = (float) pheasent.getPercentToAdulthood();

        if (percent < 0.65f)
        {
            return CHICK_TEXTURE;
        }
        else if (pheasent.getGender() == EntityAnimalTFC.Gender.MALE)
        {
            return MALE_TEXTURE;
        }
        else
        {
            return FEMALE_TEXTURE;
        }
    }

    @Override
    protected float handleRotationFloat(EntityPheasantTFC livingBase, float partialTicks)
    {
        float f = livingBase.oFlap + (livingBase.wingRotation - livingBase.oFlap) * partialTicks;
        float f1 = livingBase.oFlapSpeed + (livingBase.destPos - livingBase.oFlapSpeed) * partialTicks;
        return (MathHelper.sin(f) + 1.0F) * f1;
    }

    @Override
    protected void preRenderCallback(EntityPheasantTFC bear, float par2)
    {
        GlStateManager.scale(0.7f, 0.7f, 0.7f);
        GlStateManager.rotate(90, 0, 1, 0);
    }
}