/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.animal.ModelMuskOxWoolTFC;
import net.dries007.tfc.objects.entity.animal.EntityMuskOxTFC;


@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class LayerMuskOxWoolTFC implements LayerRenderer<EntityMuskOxTFC>
{
    private final RenderMuskOxTFC muskoxRenderer;
    private final ModelMuskOxWoolTFC muskoxModel = new ModelMuskOxWoolTFC();

    public LayerMuskOxWoolTFC(RenderMuskOxTFC renderer)
    {
        this.muskoxRenderer = renderer;
    }

    @Override
    public void doRenderLayer(EntityMuskOxTFC muskox, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (muskox.hasWool() && !muskox.isInvisible())
        {
            this.muskoxRenderer.bindTexture(this.muskoxRenderer.getEntityTexture(muskox));

            float[] afloat = EntitySheep.getDyeRgb(muskox.getDyeColor());
            GlStateManager.color(afloat[0], afloat[1], afloat[2]);

            this.muskoxModel.setModelAttributes(this.muskoxRenderer.getMainModel());
            this.muskoxModel.setLivingAnimations(muskox, limbSwing, limbSwingAmount, partialTicks);
            this.muskoxModel.render(muskox, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return true;
    }
}