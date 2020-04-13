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

import net.dries007.tfc.client.model.animal.ModelAlpacaWoolTFC;
import net.dries007.tfc.objects.entity.animal.EntityAlpacaTFC;


@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class LayerAlpacaWoolTFC implements LayerRenderer<EntityAlpacaTFC>
{
    private final RenderAlpacaTFC alpacaRenderer;
    private final ModelAlpacaWoolTFC alpacaModel = new ModelAlpacaWoolTFC();

    public LayerAlpacaWoolTFC(RenderAlpacaTFC renderer)
    {
        this.alpacaRenderer = renderer;
    }

    @Override
    public void doRenderLayer(EntityAlpacaTFC alpaca, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (alpaca.hasWool() && !alpaca.isInvisible())
        {
            this.alpacaRenderer.bindTexture(this.alpacaRenderer.getEntityTexture(alpaca));

            float[] afloat = EntitySheep.getDyeRgb(alpaca.getDyeColor());
            GlStateManager.color(afloat[0], afloat[1], afloat[2]);

            this.alpacaModel.setModelAttributes(this.alpacaRenderer.getMainModel());
            this.alpacaModel.setLivingAnimations(alpaca, limbSwing, limbSwingAmount, partialTicks);
            this.alpacaModel.render(alpaca, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return true;
    }
}