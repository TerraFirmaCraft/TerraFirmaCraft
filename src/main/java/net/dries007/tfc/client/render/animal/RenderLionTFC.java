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
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.animal.ModelLionTFC;
import net.dries007.tfc.objects.entity.animal.EntityLionTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderLionTFC extends RenderLiving<EntityLionTFC>
{
    private static final ResourceLocation LIONS_TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/predators/lions.png");

    public RenderLionTFC(RenderManager manager)
    {
        super(manager, new ModelLionTFC(), 0.3F);
    }

    @Override
    public void doRender(EntityLionTFC lion, double par2, double par4, double par6, float par8, float par9)
    {
        this.shadowSize = (float) (0.4f + lion.getPercentToAdulthood() * 0.4f);
        super.doRender(lion, par2, par4, par6, par8, par9);
    }

    protected void preRenderCallback(EntityLionTFC lionTFC, float par2)
    {
        GlStateManager.scale(1.1f, 1.1f, 1.1f);
    }


    protected ResourceLocation getEntityTexture(EntityLionTFC lion)
    {
        return LIONS_TEXTURE;
    }
}