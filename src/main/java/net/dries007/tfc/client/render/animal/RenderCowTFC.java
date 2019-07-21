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

import net.dries007.tfc.client.model.animal.ModelCowTFC;
import net.dries007.tfc.objects.entity.animal.EntityCowTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

@SideOnly(Side.CLIENT)
@ParametersAreNonnullByDefault
public class RenderCowTFC extends RenderLiving<EntityCowTFC>
{
    private static final ResourceLocation COW_TEXTURES = new ResourceLocation(MOD_ID, "textures/entity/animal/cow.png");

    public RenderCowTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelCowTFC(), 0.7F);
    }

    @Override
    protected ResourceLocation getEntityTexture(EntityCowTFC entity)
    {
        return COW_TEXTURES;
    }
}
