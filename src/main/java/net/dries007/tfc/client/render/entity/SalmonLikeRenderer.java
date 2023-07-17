/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.SalmonRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.Salmon;

import net.dries007.tfc.util.Helpers;

public class SalmonLikeRenderer extends SalmonRenderer
{
    private final ResourceLocation texture;

    public SalmonLikeRenderer(EntityRendererProvider.Context context, String name)
    {
        super(context);
        texture = Helpers.animalTexture(name);
    }

    @Override
    public ResourceLocation getTextureLocation(Salmon salmon)
    {
        return texture;
    }
}
