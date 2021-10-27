/*
 * Licensed under the EUPL, Version 1.2.
 * You may obtain a copy of the Licence at:
 * https://joinup.ec.europa.eu/collection/eupl/eupl-text-eupl-12
 */

package net.dries007.tfc.client.render;

import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.resources.ResourceLocation;

import net.dries007.tfc.client.ClientHelpers;
import net.dries007.tfc.client.model.HorseshoeCrabModel;
import net.dries007.tfc.common.entities.aquatic.AquaticCritterEntity;

public class HorseshoeCrabRenderer extends MobRenderer<AquaticCritterEntity, HorseshoeCrabModel>
{
    private static final ResourceLocation LOCATION = ClientHelpers.animalTexture("horseshoe_crab");

    public HorseshoeCrabRenderer(EntityRendererProvider.Context ctx)
    {
        super(ctx, new HorseshoeCrabModel(ctx.bakeLayer(ClientHelpers.modelIdentifier("horseshoe_crab"))), 0.3F);
    }

    @Override
    public ResourceLocation getTextureLocation(AquaticCritterEntity entity)
    {
        return LOCATION;
    }
}
