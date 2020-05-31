/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.animal.ModelSeaLionTFC;
import net.dries007.tfc.objects.entity.animal.EntitySeaLionTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderSeaLionTFC extends RenderLiving<EntitySeaLionTFC>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID, "textures/entity/animal/huntable/sealion.png");

    public RenderSeaLionTFC(RenderManager renderManager) { super(renderManager, new ModelSeaLionTFC(), 0.7F); }

    @Override
    public void doRender(@Nonnull EntitySeaLionTFC sealion, double par2, double par4, double par6, float par8, float par9)
    {
        this.shadowSize = (float) (0.35f + (sealion.getPercentToAdulthood() * 0.35f));
        super.doRender(sealion, par2, par4, par6, par8, par9);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntitySeaLionTFC entity)
    {
        return TEXTURE;
    }
}