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

import net.dries007.tfc.client.model.animal.ModelAlpacaBodyTFC;
import net.dries007.tfc.objects.entity.animal.EntityAlpacaTFC;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;


@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderAlpacaTFC extends RenderAnimalTFC<EntityAlpacaTFC>
{
    private static final ResourceLocation ALPACA_OLD = new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/alpaca_old.png");
    private static final ResourceLocation ALPACA_YOUNG = new ResourceLocation(MOD_ID, "textures/entity/animal/livestock/alpaca_young.png");

    public RenderAlpacaTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelAlpacaBodyTFC(), 0.7F, ALPACA_YOUNG, ALPACA_OLD);
        this.addLayer(new LayerAlpacaWoolTFC(this));
    }
}