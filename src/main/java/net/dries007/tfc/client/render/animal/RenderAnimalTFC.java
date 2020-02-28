/*
 * Work under Copyright. Licensed under the EUPL.
 * See the project README.md and LICENSE.txt for more information.
 */

package net.dries007.tfc.client.render.animal;

import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;

import net.minecraft.client.model.ModelBase;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.ResourceLocation;

import net.dries007.tfc.api.types.IAnimalTFC;

@SuppressWarnings("WeakerAccess")
@ParametersAreNonnullByDefault
public abstract class RenderAnimalTFC<T extends EntityLiving> extends RenderLiving<T>
{
    private final ResourceLocation youngTexture;
    private final ResourceLocation oldTexture;

    protected RenderAnimalTFC(RenderManager rendermanagerIn, ModelBase modelbaseIn, float shadowsizeIn, @Nonnull ResourceLocation youngTextures, @Nonnull ResourceLocation oldTextures)
    {
        super(rendermanagerIn, modelbaseIn, shadowsizeIn);
        this.youngTexture = youngTextures;
        this.oldTexture = oldTextures;
    }

    @Nonnull
    @Override
    protected ResourceLocation getEntityTexture(T entity)
    {
        if (entity instanceof IAnimalTFC && ((IAnimalTFC) entity).getAge() == IAnimalTFC.Age.OLD)
        {
            return oldTexture;
        }
        return youngTexture;
    }
}
