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

import net.dries007.tfc.client.model.animal.ModelChickenTFC;
import net.dries007.tfc.objects.entity.animal.EntityAnimalTFC;
import net.dries007.tfc.objects.entity.animal.EntityChickenTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderChickenTFC extends RenderLiving<EntityChickenTFC>
{
    private static final ResourceLocation CHICKEN_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/chicken.png");
    private static final ResourceLocation ROOSTER_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/rooster.png");
    private static final ResourceLocation CHICK_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/chick.png");

    public RenderChickenTFC(RenderManager manager)
    {
        super(manager, new ModelChickenTFC(), 0.3F);
    }

    @Override
    public void doRender(EntityChickenTFC chicken, double par2, double par4, double par6, float par8, float par9)
    {
        this.shadowSize = 0.15f + chicken.getPercentToAdulthood() * 0.15f;
        super.doRender(chicken, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityChickenTFC chicken)
    {
        float percent = chicken.getPercentToAdulthood();

        if (percent < 0.65f)
        {
            return CHICK_TEXTURE;
        }
        else if (chicken.getGender() == EntityAnimalTFC.Gender.MALE)
        {
            return ROOSTER_TEXTURE;
        }
        else
        {
            return CHICKEN_TEXTURE;
        }
    }

    @Override
    protected float handleRotationFloat(EntityChickenTFC livingBase, float partialTicks)
    {
        float f = livingBase.oFlap + (livingBase.wingRotation - livingBase.oFlap) * partialTicks;
        float f1 = livingBase.oFlapSpeed + (livingBase.destPos - livingBase.oFlapSpeed) * partialTicks;
        return (MathHelper.sin(f) + 1.0F) * f1;
    }

    @Override
    protected void preRenderCallback(EntityChickenTFC bear, float par2)
    {
        GlStateManager.scale(0.7f, 0.7f, 0.7f);
    }
}