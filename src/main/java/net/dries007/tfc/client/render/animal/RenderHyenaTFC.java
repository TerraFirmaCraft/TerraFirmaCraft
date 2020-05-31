package net.dries007.tfc.client.render.animal;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.animal.ModelHyenaTFC;
import net.dries007.tfc.objects.entity.animal.EntityHyenaTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderHyenaTFC extends RenderLiving<EntityHyenaTFC>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/predators/hyena.png");

    public RenderHyenaTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelHyenaTFC(), 0.7F);
    }

    @Override
    public void doRender(@Nonnull EntityHyenaTFC hyena, double par2, double par4, double par6, float par8, float par9)
    {
        this.shadowSize = (float) (0.35f + (hyena.getPercentToAdulthood() * 0.35f));
        super.doRender(hyena, par2, par4, par6, par8, par9);
    }

    @Override
    protected float handleRotationFloat(EntityHyenaTFC par1EntityLiving, float par2)
    {
        return 1.0f;
    }

    @Override
    protected void preRenderCallback(EntityHyenaTFC hyenaTFC, float par2)
    {
        GlStateManager.scale(1.1f, 1.1f, 1.1f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityHyenaTFC entity)
    {
        return TEXTURE;
    }
}