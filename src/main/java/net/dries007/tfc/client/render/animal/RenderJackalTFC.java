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

import net.dries007.tfc.client.model.animal.ModelJackalTFC;
import net.dries007.tfc.objects.entity.animal.EntityJackalTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderJackalTFC extends RenderLiving<EntityJackalTFC>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/predators/jackal.png");

    public RenderJackalTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelJackalTFC(), 0.7F);
    }

    @Override
    public void doRender(@Nonnull EntityJackalTFC jackal, double par2, double par4, double par6, float par8, float par9)
    {
        this.shadowSize = (float) (0.35f + (jackal.getPercentToAdulthood() * 0.35f));
        super.doRender(jackal, par2, par4, par6, par8, par9);
    }

    @Override
    protected float handleRotationFloat(EntityJackalTFC par1EntityLiving, float par2)
    {
        return 1.0f;
    }

    @Override
    protected void preRenderCallback(EntityJackalTFC jackalTFC, float par2)
    {
        GlStateManager.scale(0.8f, 0.8f, 0.8f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityJackalTFC entity)
    {
        return TEXTURE;
    }
}