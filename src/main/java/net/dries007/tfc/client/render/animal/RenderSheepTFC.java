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

import net.dries007.tfc.client.model.animal.ModelSheepBodyTFC;
import net.dries007.tfc.objects.entity.animal.EntitySheepTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderSheepTFC extends RenderAnimalTFC<EntitySheepTFC>
{
    private static final ResourceLocation SHEEP_YOUNG = new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/sheep_young.png");
    private static final ResourceLocation SHEEP_OLD = new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/sheep_old.png");

    public RenderSheepTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelSheepBodyTFC(), 0.7F, SHEEP_YOUNG, SHEEP_OLD);
        this.addLayer(new LayerSheepWoolTFC(this));
    }
}
