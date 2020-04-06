/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.animal.ModelPolarBearTFC;
import net.dries007.tfc.objects.entity.animal.EntityPolarBearTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
public class RenderPolarBearTFC extends RenderAnimalTFC<EntityPolarBearTFC>
{
    private static final ResourceLocation YOUNG = new ResourceLocation(MOD_ID, "textures/entity/animal/polarbear_young.png");
    private static final ResourceLocation OLD = new ResourceLocation(MOD_ID, "textures/entity/animal/polarbear_old.png");

    public RenderPolarBearTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelPolarBearTFC(), 0.7F, YOUNG, OLD);
    }

    @Override
    public void doRender(@Nonnull EntityPolarBearTFC polarBearTFC, double par2, double par4, double par6, float par8, float par9)
    {
        this.shadowSize = (float) (0.35f + (polarBearTFC.getPercentToAdulthood() * 0.35f));
        super.doRender(polarBearTFC, par2, par4, par6, par8, par9);
    }

    @Override
    protected float handleRotationFloat(EntityPolarBearTFC par1EntityLiving, float par2)
    {
        return 1.0f;
    }

    @Override
    protected void preRenderCallback(EntityPolarBearTFC polarBearTFC, float par2)
    {
        GlStateManager.scale(1.3f, 1.3f, 1.3f);
    }
}
