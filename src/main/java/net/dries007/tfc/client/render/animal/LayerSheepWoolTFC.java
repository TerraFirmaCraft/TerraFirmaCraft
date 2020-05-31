/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.passive.EntitySheep;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.api.types.IAnimalTFC;
import net.dries007.tfc.client.model.animal.ModelSheepWoolTFC;
import net.dries007.tfc.objects.entity.animal.EntitySheepTFC;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class LayerSheepWoolTFC implements LayerRenderer<EntitySheepTFC>
{
    private static final ResourceLocation TEXTURE = new ResourceLocation("minecraft:textures/entity/sheep/sheep_fur.png");
    private static final ResourceLocation OLD_TEXTURE = new ResourceLocation("tfc:textures/entity/animal/livestock/sheep_fur_old.png");
    private final RenderSheepTFC sheepRenderer;
    private final ModelSheepWoolTFC sheepModel = new ModelSheepWoolTFC();

    public LayerSheepWoolTFC(RenderSheepTFC sheepRendererIn)
    {
        this.sheepRenderer = sheepRendererIn;
    }

    @Override
    public void doRenderLayer(EntitySheepTFC sheep, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (sheep.hasWool() && !sheep.isInvisible())
        {
            this.sheepRenderer.bindTexture(sheep.getAge() == IAnimalTFC.Age.OLD ? OLD_TEXTURE : TEXTURE);

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