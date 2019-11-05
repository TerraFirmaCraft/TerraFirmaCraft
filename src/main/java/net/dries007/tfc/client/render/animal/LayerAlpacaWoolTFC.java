/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import net.dries007.tfc.client.model.animal.ModelAlpacaWoolTFC;
import net.dries007.tfc.objects.entity.animal.EntityAlpacaTFC;
import net.dries007.tfc.objects.entity.animal.EntityAlpacaWoolTFC;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;
import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class LayerAlpacaWoolTFC implements LayerRenderer<EntityAlpacaTFC>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation(MOD_ID,"textures/entity/animal/alpaca.png");
    private final RenderAlpacaTFC alpacaRenderer;
    private final ModelAlpacaWoolTFC alpacaModel = new ModelAlpacaWoolTFC();

    public LayerAlpacaWoolTFC(RenderAlpacaTFC alpacaRendererIn)
    {
        this.alpacaRenderer = alpacaRendererIn;
    }

    @Override
    public void doRenderLayer(EntityAlpacaTFC alpaca, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (alpaca.hasWool() && !alpaca.isInvisible())
        {
            this.alpacaRenderer.bindTexture(TEXTURE);

            float[] afloat = EntityAlpacaWoolTFC.getDyeRgb(alpaca.getDyeColor());
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