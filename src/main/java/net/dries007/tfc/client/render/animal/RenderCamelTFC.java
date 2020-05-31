/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.animal.ModelCamelTFC;
import net.dries007.tfc.objects.entity.animal.EntityCamelTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderCamelTFC extends RenderAnimalTFC<EntityCamelTFC>
{
    private static final ResourceLocation OLD = new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/camel_old.png");
    private static final ResourceLocation YOUNG = new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/camel_young.png");

    public RenderCamelTFC(RenderManager p_i47203_1_)
    {
        super(p_i47203_1_, new ModelCamelTFC(0.0F), 0.7F, YOUNG, OLD);
        this.addLayer(new LayerCamelDecor(this));
    }

}