/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import java.util.stream.IntStream;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.animal.ModelCamelTFC;
import net.dries007.tfc.objects.entity.animal.EntityCamelTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;


@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class LayerCamelDecor implements LayerRenderer<EntityCamelTFC>
{
    private static final ResourceLocation[] CAMEL_DECOR_TEXTURES = IntStream.range(0, 16).mapToObj(i -> new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/decor/" + EnumDyeColor.byMetadata(i).getName() + ".png")).toArray(ResourceLocation[]::new);
    private final RenderCamelTFC renderer;
    private final ModelCamelTFC model = new ModelCamelTFC(0.51F);

    public LayerCamelDecor(RenderCamelTFC p_i47184_1_)
    {
        this.renderer = p_i47184_1_;
    }

    public void doRenderLayer(EntityCamelTFC entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale)
    {
        if (entitylivingbaseIn.hasColor())
        {
            this.renderer.bindTexture(CAMEL_DECOR_TEXTURES[entitylivingbaseIn.getColor().getMetadata()]);
            this.model.setModelAttributes(this.renderer.getMainModel());
            this.model.render(entitylivingbaseIn, limbSwing, limbSwingAmount, ageInTicks, netHeadYaw, headPitch, scale);
        }

    }

    public boolean shouldCombineTextures()
    {
        return false;
    }
}
