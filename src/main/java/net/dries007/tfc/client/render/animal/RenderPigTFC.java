/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.animal.ModelPigTFC;
import net.dries007.tfc.objects.entity.animal.EntityPigTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderPigTFC extends RenderLiving<EntityPigTFC>
{
    private static final ResourceLocation PIG_TEXTURES = new ResourceLocation(MOD_ID, "textures/entity/animal/pig.png");

    public RenderPigTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelPigTFC(), 0.7F);
    }

    @Override
    public void doRender(EntityPigTFC pig, double par2, double par4, double par6, float par8, float par9)
    {
        this.shadowSize = 0.35f + pig.getPercentToAdulthood() * 0.35f;
        super.doRender(pig, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityPigTFC entity)
    {
        return PIG_TEXTURES;
    }
}