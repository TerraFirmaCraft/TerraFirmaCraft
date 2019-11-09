/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.animal.ModelAlpacaBodyTFC;
import net.dries007.tfc.objects.entity.animal.EntityAlpacaTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderAlpacaTFC extends RenderLiving<EntityAlpacaTFC>
{
    private static final ResourceLocation ALPACA_TEXTURES = new ResourceLocation(MOD_ID, "textures/entity/animal/alpaca.png");

    public RenderAlpacaTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelAlpacaBodyTFC(), 0.7F);
        this.addLayer(new LayerAlpacaWoolTFC(this));
    }

    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityAlpacaTFC entity)
    {
        return ALPACA_TEXTURES;
    }
}
