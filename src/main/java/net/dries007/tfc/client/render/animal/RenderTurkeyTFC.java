/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.animal.ModelTurkeyTFC;
import net.dries007.tfc.objects.entity.animal.EntityAnimalTFC;
import net.dries007.tfc.objects.entity.animal.EntityTurkeyTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderTurkeyTFC extends RenderLiving<EntityTurkeyTFC>
{
    private static final ResourceLocation MALE = new ResourceLocation(MOD_ID, "textures/entity/animal/huntable/turkeym.png");
    private static final ResourceLocation FEMALE = new ResourceLocation(MOD_ID, "textures/entity/animal/huntable/turkeyf.png");

    public RenderTurkeyTFC(RenderManager manager)
    {
        super(manager, new ModelTurkeyTFC(), 0.5F);
    }

    @Override
    public void doRender(@Nonnull EntityTurkeyTFC turkey, double par2, double par4, double par6, float par8, float par9)
    {
        this.shadowSize = (float) (0.35f + (turkey.getPercentToAdulthood() * 0.35f));
        super.doRender(turkey, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityTurkeyTFC turkey)
    {
        if (turkey.getGender() == EntityAnimalTFC.Gender.MALE)
        {
            return MALE;
        }
        else
        {
            return FEMALE;
        }
    }

    @Override
    protected void preRenderCallback(EntityTurkeyTFC tukeyTFC, float par2)
    {
        GlStateManager.scale(0.8f, 0.8f, 0.8f);
    }
}