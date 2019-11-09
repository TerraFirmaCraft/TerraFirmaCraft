/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

import net.dries007.tfc.client.model.animal.ModelGoatTFC;
import net.dries007.tfc.objects.entity.animal.EntityGoatTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderGoatTFC extends RenderLiving<EntityGoatTFC>
{
    private static final ResourceLocation GOAT_TEXTURES = new ResourceLocation(MOD_ID, "textures/entity/animal/goat.png");

    public RenderGoatTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelGoatTFC(), 0.7F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityGoatTFC entity)
    {
        return GOAT_TEXTURES;
    }
}
