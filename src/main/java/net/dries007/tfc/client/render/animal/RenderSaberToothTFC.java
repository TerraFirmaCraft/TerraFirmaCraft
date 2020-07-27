package net.dries007.tfc.client.render.animal;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.animal.ModelSaberToothTFC;
import net.dries007.tfc.objects.entity.animal.EntitySaberToothTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderSaberToothTFC extends RenderLiving<EntitySaberToothTFC>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/predators/sabertooth.png");

    public RenderSaberToothTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelSaberToothTFC(), 0.7F);
    }

    @Override
    public void doRender(@Nonnull EntitySaberToothTFC sabertooth, double par2, double par4, double par6, float par8, float par9)
    {
        this.shadowSize = (float) (0.35f + (sabertooth.getPercentToAdulthood() * 0.35f));
        super.doRender(sabertooth, par2, par4, par6, par8, par9);
    }

    @Override
    protected float handleRotationFloat(EntitySaberToothTFC par1EntityLiving, float par2)
    {
        return 1.0f;
    }

    @Override
    protected void preRenderCallback(EntitySaberToothTFC sabertooth, float par2)
    {
        GlStateManager.scale(1.3f, 1.3f, 1.3f);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySaberToothTFC entity)
    {
        return TEXTURE;
    }
}