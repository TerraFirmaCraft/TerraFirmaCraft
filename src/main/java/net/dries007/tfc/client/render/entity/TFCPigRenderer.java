/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render.entity;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.model.entity.TFCPigModel;
import net.dries007.tfc.common.entities.land.TFCPig;

public class TFCPigRenderer extends MobRenderer<TFCPig, TFCPigModel>
{
    private static final ResourceLocation YOUNG = ClientHelpers.animalTexture("pig_young");
    private static final ResourceLocation OLD = ClientHelpers.animalTexture("pig_old");

    public TFCPigRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new TFCPigModel(ctx.bakeLayer(ClientHelpers.modelIdentifier("pig"))), 0.7F);
        //todo: saddle layer?
    }

    @Override
    public ResourceLocation getTextureLocation(TFCPig pEntity)
    {
        return ClientHelpers.getTextureForAge(pEntity, YOUNG, OLD);
    }
}
