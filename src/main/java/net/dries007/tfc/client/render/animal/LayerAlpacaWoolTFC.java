/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import net.dries007.tfc.client.model.animal.ModelAlpacaWoolTFC;
import net.dries007.tfc.objects.entity.animal.EntityAlpacaTFC;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import javax.annotation.ParametersAreNonnullByDefault;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class LayerAlpacaWoolTFC implements LayerRenderer<EntityAlpacaTFC>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft:textures/entity/sheep/sheep_fur.png");
    private final RenderAlpacaTFC sheepRenderer;
    private final ModelAlpacaWoolTFC sheepModel = new ModelAlpacaWoolTFC();

    public LayerAlpacaWoolTFC(RenderAlpacaTFC sheepRendererIn)
    {
        this.sheepRenderer = sheepRendererIn;
    }

    @Override
    public void doRenderLayer(EntityAlpacaTFC sheep, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (sheep.hasWool() && !sheep.isInvisible())
        {
            this.sheepRenderer.bindTexture(TEXTURE);

            float[] afloat = EntitySheep.getDyeRgb(sheep.getDyeColor());
            GlStateManager.color(afloat[0], afloat[1], afloat[2]);

            this.sheepModel.setModelAttributes(this.sheepRenderer.getMainModel());
            this.sheepModel.setLivingAnimations(sheep, limbSwing, limbSwingAmount, partialTicks);
            this.sheepModel.render(sheep, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }
    }

    @Override
    public boolean shouldCombineTextures()
    {
        return true;
    }
}