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

import net.dries007.tfc.client.model.animal.ModelMuskOxTFC;
import net.dries007.tfc.objects.entity.animal.EntityMuskOxTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderMuskOxTFC extends RenderAnimalTFC<EntityMuskOxTFC>
{
    private static final ResourceLocation TEXTURE_YOUNG = new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/muskox_young.png");
    private static final ResourceLocation TEXTURE_OLD = new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/muskox_old.png");

    public RenderMuskOxTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelMuskOxTFC(), 0.7F, TEXTURE_YOUNG, TEXTURE_OLD);
    }
}
