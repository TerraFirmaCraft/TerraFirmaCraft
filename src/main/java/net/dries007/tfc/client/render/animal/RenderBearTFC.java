/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.animal.ModelBearTFC;
import net.dries007.tfc.objects.entity.animal.EntityBearTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderBearTFC extends RenderAnimalTFC<EntityBearTFC>
{
    private static final ResourceLocation YOUNG = new ResourceLocation(MOD_ID, "textures/entity/animal/bear_young.png");
    private static final ResourceLocation OLD = new ResourceLocation(MOD_ID, "textures/entity/animal/bear_old.png");

    public RenderBearTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelBearTFC(), 0.7F, YOUNG, OLD);
    }

    @Override
    public void doRender(@Nonnull EntityBearTFC bear, double par2, double par4, double par6, float par8, float par9)
    {
        this.shadowSize = (float) (0.35f + (bear.getPercentToAdulthood() * 0.35f));
        super.doRender(bear, par2, par4, par6, par8, par9);
    }

    @Override
    protected float handleRotationFloat(EntityBearTFC par1EntityLiving, float par2)
    {
        return 1.0f;
    }

    @Override
    protected void preRenderCallback(EntityBearTFC bear, float par2)
    {
        GlStateManager.scale(1.3f, 1.3f, 1.3f);
    }
}