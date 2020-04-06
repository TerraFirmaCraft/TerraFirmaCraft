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
    private static final ResourceLocation CAMEL_OLD = new ResourceLocation(MOD_ID, "textures/entity/animal/camel_old.png");
    private static final ResourceLocation CAMEL_YOUNG = new ResourceLocation(MOD_ID, "textures/entity/animal/camel_young.png");

    public RenderCamelTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelCamelTFC(), 0.7F, CAMEL_YOUNG, CAMEL_OLD);
    }
}