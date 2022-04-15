/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.RenderHelpers;
import net.dries007.tfc.common.entities.land.TFCAnimal;
import net.dries007.tfc.common.entities.land.TFCAnimalProperties;
import org.jetbrains.annotations.Nullable;

public class GenderedRenderer<T extends TFCAnimal, M extends EntityModel<T>> extends AnimalRenderer<T, M>
{
    @Nullable
    private final ResourceLocation maleYoung;
    @Nullable
    private final ResourceLocation maleOld;

    public GenderedRenderer(EntityRendererProvider.Context ctx, M model, String name)
    {
        this(ctx, model, name, null);
    }

    public GenderedRenderer(EntityRendererProvider.Context ctx, M model, String name, @Nullable String maleName)
    {
        super(ctx, model, name);
        maleYoung = maleName == null ? null : RenderHelpers.animalTexture(maleName + "_young");
        maleOld = maleName == null ? null : RenderHelpers.animalTexture(maleName + "_old");
    }

    @Override
    public ResourceLocation getTextureLocation(T entity)
    {
        return maleYoung != null && maleOld != null && entity.getGender() == TFCAnimalProperties.Gender.MALE ? RenderHelpers.getTextureForAge(entity, maleYoung, maleOld) : super.getTextureLocation(entity);
    }
}
