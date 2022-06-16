/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import net.minecraft.client.model.EntityModel;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.util.Mth;

import net.dries007.tfc.common.entities.livestock.OviparousAnimal;
import org.jetbrains.annotations.Nullable;

public class OviparousRenderer<T extends OviparousAnimal, M extends EntityModel<T>> extends GenderedRenderer<T, M>
{
    public OviparousRenderer(EntityRendererProvider.Context ctx, M model, String name)
    {
        this(ctx, model, name, null);
    }

    public OviparousRenderer(EntityRendererProvider.Context ctx, M model, String name, @Nullable String maleName)
    {
        this(ctx, model, name, maleName, null);
    }

    public OviparousRenderer(EntityRendererProvider.Context ctx, M model, String name, @Nullable String maleName, @Nullable String babyName)
    {
        super(ctx, model, name, maleName, babyName);
    }

    @Override
    protected float getBob(OviparousAnimal animal, float amount)
    {
        float f = Mth.lerp(amount, animal.oFlap, animal.flap);
        float f1 = Mth.lerp(amount, animal.oFlapSpeed, animal.flapSpeed);
        return (Mth.sin(f) + 1.0F) * f1;
    }
}
