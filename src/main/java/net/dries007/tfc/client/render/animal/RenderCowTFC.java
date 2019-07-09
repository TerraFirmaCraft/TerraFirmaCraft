/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import javax.annotation.Nonnull;

import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.client.model.animal.ModelCowTFC;
import net.dries007.tfc.objects.entity.animal.EntityCowTFC;

import static net.dries007.tfc.api.util.TFCConstants.MOD_ID;

public class RenderCowTFC  extends RenderLiving<EntityCowTFC>
{
    private static final ResourceLocation COW_TEXTURES = new ResourceLocation(MOD_ID, "textures/entity/animal/cow.png");

    public RenderCowTFC(RenderManager renderManager)
    {
        super(renderManager, new ModelCowTFC(), 0.7F);
    }

    /**
     * Returns the location of an entity's texture. Doesn't seem to be called unless you call Render.bindEntityTexture.
     */
    @Override
    protected ResourceLocation getEntityTexture(@Nonnull EntityCowTFC entity)
    {
        return COW_TEXTURES;
    }
}
