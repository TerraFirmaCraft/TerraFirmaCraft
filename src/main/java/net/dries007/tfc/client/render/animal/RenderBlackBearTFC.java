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

import net.dries007.tfc.client.model.animal.ModelBlackBearTFC;
import net.dries007.tfc.objects.entity.animal.EntityBlackBearTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderBlackBearTFC extends RenderLiving<EntityBlackBearTFC>
{
    private static final ResourceLocation BLACKBEAR_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/predators/blackbear.png");

    public RenderBlackBearTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelBlackBearTFC(), 0.7F);
    }

    @Override
    public void doRender(@Nonnull EntityBlackBearTFC blackbear, double par2, double par4, double par6, float par8, float par9)
    {
        this.shadowSize = (float) (0.35f + (blackbear.getPercentToAdulthood() * 0.35f));
        super.doRender(blackbear, par2, par4, par6, par8, par9);
    }

    @Override
    protected float handleRotationFloat(EntityBlackBearTFC par1EntityLiving, float par2)
    {
        return 1.0f;
    }

    @Override
    protected void preRenderCallback(EntityBlackBearTFC blackbearTFC, float par2)
    {
        GlStateManager.scale(1.4f, 1.4f, 1.4f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityBlackBearTFC entity)
    {
        return BLACKBEAR_TEXTURE;
    }
}