/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.animal.camel.Camel;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.client.model.entity.HierarchicalAnimatedModel;
import net.dries007.tfc.common.entities.livestock.Age;
import net.dries007.tfc.common.entities.livestock.camel.AbstractCamel;

public class BactrianCamelRenderer<T extends Camel, M extends HierarchicalAnimatedModel<T>> extends MobRenderer<T, M>
{
    private final ResourceLocation young;
    private final ResourceLocation old;
    private final ResourceLocation saddled;
    private final ResourceLocation old_saddled;

    public BactrianCamelRenderer(EntityRendererProvider.Context ctx, M model, float shadow)
    {
        super(ctx, model, shadow);
        this.young = RenderHelpers.animalTexture("bactrian_camel_young");
        this.old = RenderHelpers.animalTexture("bactrian_camel_old");
        this.saddled = RenderHelpers.animalTexture("bactrian_camel_saddle");
        this.old_saddled = RenderHelpers.animalTexture("bactrian_camel_old_saddle");
    }

    @Override
    public ResourceLocation getTextureLocation(T entity)
    {
        if (entity instanceof AbstractCamel camel) {
            if (camel.isSaddled())
            {
                return camel.getAgeType() == Age.OLD ? old_saddled : saddled;
            }
            else return camel.getAgeType() == Age.OLD ? old : young;
        }
        else return young;
    }
}
